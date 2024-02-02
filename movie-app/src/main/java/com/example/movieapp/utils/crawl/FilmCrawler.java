package com.example.movieapp.utils.crawl;

import com.example.movieapp.entity.*;
import com.example.movieapp.model.enums.FilmAccessType;
import com.example.movieapp.model.enums.FilmType;
import com.example.movieapp.repository.*;
import com.github.javafaker.Faker;
import com.github.slugify.Slugify;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmCrawler {
    private final Slugify slugify;
    private final GenreRepository genreRepository;
    private final ActorRepository actorRepository;
    private final DirectorRepository directorRepository;
    private final FilmRepository filmRepository;
    private final ReviewCrawler reviewCrawler;

    public void crawlFilm(String url) {
        try {
            log.info("Crawling film from: {}", url);
            Random random = new Random();
            Document doc = Jsoup.connect(url).get();

            String title = doc.selectFirst(".jsx-9e4ccf1f4860abb8.mt-2.text-2xl.font-bold.text-white").text();
            String slug = slugify.slugify(title);
            String description = doc.selectFirst(".jsx-9e4ccf1f4860abb8.mt-1.text-sm.leading-relaxed.text-white.text-opacity-70").text();
            String poster = doc.selectFirst("meta[property=og:image]").attr("content");

            // Random year from 2021 to 2023
            Integer releaseYear = random.nextInt(3) + 2021;

            // Random view from 1000 to 9000
            Integer view = random.nextInt(8000) + 1000;

            // Random rating from 5 to 10
            Double rating = random.nextInt(5) + 5.0;

            // Random type
            FilmType type = FilmType.values()[random.nextInt(FilmType.values().length)];

            // Random status
            Boolean status = true;

            // Random access type
            FilmAccessType accessType = FilmAccessType.PAID;

            // Random price
            Integer price = randomPrice();

            // Lấy danh sách thể loại
            Element genreElements = doc.select(".jsx-9e4ccf1f4860abb8.mt-1.font-bold.text-white.text-opacity-90").get(1);
            Set<Genre> genreList = parseGenre(genreElements);

            // Lấy danh sách đạo diễn
            Elements people = doc.select(".jsx-1515330669.actor-col");

            List<Director> directorList = new ArrayList<>();
            if (people != null && !people.isEmpty()) {
                Element directorElement = people.get(0);
                Director director = parseDirector(directorElement);
                directorList.add(director);
            }


            // Lấy danh sách diễn viên
            List<Actor> actorList = new ArrayList<>();
            if (people != null && people.size() > 1) {
                List<Element> actorElements = people.subList(1, people.size());
                actorList = parseActor(actorElements);
            }

            // Lưu vào database
            Film film = Film.builder()
                    .title(title)
                    .slug(slug)
                    .description(description)
                    .poster(poster)
                    .releaseYear(releaseYear)
                    .view(view)
                    .rating(rating)
                    .type(type)
                    .accessType(accessType)
                    .price(price)
                    .status(status)
                    .genres(genreList)
                    .directors(new HashSet<>(directorList))
                    .actors(new HashSet<>(actorList))
                    .build();
            filmRepository.save(film);

            // Crawl review
            reviewCrawler.crawlReviewsOfFilm(url, film);

        } catch (IOException e) {
            log.error("Error crawling film: {}", e.getMessage());
        }
    }

    private List<Actor> parseActor(List<Element> actorElements) {
        Faker faker = new Faker();
        List<Actor> actorList = new ArrayList<>();
        actorElements.forEach(actorElement -> {
            String name = actorElement.selectFirst(".mt-1.mb-1.text-sm.leading-tight ").text();
            String avatar = actorElement.selectFirst(".absolute.inset-0.h-20.w-20.object-cover").attr("src");

            Actor actor = Actor.builder()
                    .name(name)
                    .description(faker.lorem().paragraph())
                    .birthday(faker.date().birthday())
                    .avatar(avatar)
                    .build();
            actorList.add(actor);
        });

        // Nếu chưa có thì lưu vào database
        List<Actor> actorListReturned = new ArrayList<>();
        actorList.forEach(actor -> {
            if (!actorRepository.existsByName(actor.getName())) {
                actorRepository.save(actor);
                actorListReturned.add(actor);
            } else {
                actorListReturned.add(actorRepository.findByName(actor.getName()).get());
            }
        });
        return actorListReturned;
    }

    private Director parseDirector(Element directorElement) {
        Faker faker = new Faker();
        String name = directorElement.selectFirst(".mt-1.mb-1.text-sm.leading-tight ").text();
        String avatar = directorElement.selectFirst(".absolute.inset-0.h-20.w-20.object-cover").attr("src");

        Director director = Director.builder()
                .name(name)
                .description(faker.lorem().paragraph())
                .birthday(faker.date().birthday())
                .avatar(avatar)
                .build();

        // Nếu chưa có thì lưu vào database
        if (!directorRepository.existsByName(director.getName())) {
            directorRepository.save(director);
        } else {
            director = directorRepository.findByName(director.getName()).get();
        }
        return director;
    }

    private Set<Genre> parseGenre(Element genreElements) {
        String string = genreElements.text();
        List<String> genreListString = new ArrayList<>(Arrays.asList(string.split(", ")));
        Set<Genre> genreList = new HashSet<>();
        genreListString.forEach(genre -> {
            if (!genreRepository.existsByName(genre)) {
                Genre genreEntity = Genre.builder()
                        .name(genre)
                        .slug(slugify.slugify(genre))
                        .build();
                genreRepository.save(genreEntity);
                genreList.add(genreEntity);
            } else {
                genreList.add(genreRepository.findByName(genre).get());
            }
        });
        return genreList;
    }

    public int randomPrice() {
        Random random = new Random();
        int price = random.nextInt(100000 - 10000 + 1) + 10000;
        price = price / 1000;
        price = price * 1000;
        return price;
    }

    public void crawlAllFilm() {
        List<String> urls = new ArrayList<>(List.of(
//                "https://momo.vn/cinema/lat-mat-6-tam-ve-dinh-menh-961",
//                "https://momo.vn/cinema/the-house-of-no-man-879",
//                "https://momo.vn/cinema/avatar-the-way-of-water-682",
//                "https://momo.vn/cinema/6-45-827",
//                "https://momo.vn/cinema/sieu-lua-gap-sieu-lay-891",
//                "https://momo.vn/cinema/chi-chi-em-em-2-892",
//                "https://momo.vn/cinema/elemental-971",
//                "https://momo.vn/cinema/marry-my-dead-body-967",
//                "https://momo.vn/cinema/suzume-no-tojimari-941",
//                "https://momo.vn/cinema/chuyen-xom-tui-con-nhot-mot-chong-938",
//                "https://momo.vn/cinema/fast-x-920",
//                "https://momo.vn/cinema/doctor-strange-in-the-multiverse-of-madness-632",
//                "https://momo.vn/cinema/love-destiny-the-movie-817",
//                "https://momo.vn/cinema/transformers-rise-of-the-beasts-880",
//                "https://momo.vn/cinema/chia-khoa-tram-ty-66",
//                "https://momo.vn/cinema/doraemon-movie-42-nobita-to-sora-no-utopia-981",
//                "https://momo.vn/cinema/vietnamese-horror-story-615",
//                "https://momo.vn/cinema/home-for-rent-1008",
//                "https://momo.vn/cinema/spiderman-no-way-home-69",
//                "https://momo.vn/cinema/antman-and-the-wasp-quantumania-851",
//                "https://momo.vn/cinema/demon-slayer-to-the-swordsmith-village-949",
//                "https://momo.vn/cinema/girl-from-the-past-816",
//                "https://momo.vn/cinema/black-adam-798",
//                "https://momo.vn/cinema/extremely-easy-job-647",
//                "https://momo.vn/cinema/minions-the-rise-of-gru-674",
//                "https://momo.vn/cinema/furies-877",
//                "https://momo.vn/cinema/em-va-trinh-658",
//                "https://momo.vn/cinema/the-popes-exorcist-953",
//                "https://momo.vn/cinema/puss-in-boots-the-last-wish-648",
//                "https://momo.vn/cinema/the-batman-572",
//                "https://momo.vn/cinema/spiderman-across-the-spiderverse-894",
//                "https://momo.vn/cinema/guardians-of-the-galaxy-volume-3-881",
//                "https://momo.vn/cinema/the-childe-1007",
//                "https://momo.vn/cinema/nha-khong-ban-612",
//                "https://momo.vn/cinema/the-little-mermaid-984",
//                "https://momo.vn/cinema/black-panther-wakanda-forever-796",
//                "https://momo.vn/cinema/vong-nhi-918",
//                "https://momo.vn/cinema/gangnam-zombie-901",
//                "https://momo.vn/cinema/doraemon-nobita-no-little-wars-2021-673",
//                "https://momo.vn/cinema/bo-gia-81",
//                "https://momo.vn/cinema/thor-love-and-thunder-665",
//                "https://momo.vn/cinema/emergency-declaration-790",
//                "https://momo.vn/cinema/the-ancestral-646",
//                "https://momo.vn/cinema/the-flash-933",
//                "https://momo.vn/cinema/one-piece-film-red-841",
//                "https://momo.vn/cinema/confidential-assignment-2-international-843",
//                "https://momo.vn/cinema/jujutsu-kaisen-0-642",
//                "https://momo.vn/cinema/missing-939",
//                "https://momo.vn/cinema/the-first-slam-dunk-976",
//                "https://momo.vn/cinema/soulmate-952",
//                "https://momo.vn/cinema/tri-am-nguoi-giu-thoi-gian-954",
//                "https://momo.vn/cinema/consecration-934",
//                "https://momo.vn/cinema/1990-80",
//                "https://momo.vn/cinema/smile-777",
//                "https://momo.vn/cinema/muoi-loi-nguyen-tro-lai-815",
//                "https://momo.vn/cinema/shazam-fury-of-the-gods-797",
//                "https://momo.vn/cinema/moonfall-610",
//                "https://momo.vn/cinema/jurassic-world-dominion-675",
//                "https://momo.vn/cinema/fantastic-beasts-the-secrets-of-dumbledore-591",
//                "https://momo.vn/cinema/ta-nang-phan-dung-416",
//                "https://momo.vn/cinema/lost-in-mekong-delta-804",
//                "https://momo.vn/cinema/conan-the-detective-the-bride-of-halloween-699",
//                "https://momo.vn/cinema/semantic-error-825",
//                "https://momo.vn/cinema/morbius-624",
//                "https://momo.vn/cinema/lat-mat-48h-88",
//                "https://momo.vn/cinema/biet-doi-rat-on-955",
//                "https://momo.vn/cinema/dont-look-at-the-demon-847",
//                "https://momo.vn/cinema/m3gan-842",
//                "https://momo.vn/cinema/top-gun-maverick-676",
//                "https://momo.vn/cinema/men-gai-mien-tay-657",
//                "https://momo.vn/cinema/kisaragi-station-820",
//                "https://momo.vn/cinema/the-super-mario-bros-movie-884",
//                "https://momo.vn/cinema/dungeons-dragons-honor-among-thieves-980",
//                "https://momo.vn/cinema/hoon-payon-1003",
//                "https://momo.vn/cinema/the-witch-part2-the-other-one-701",
//                "https://momo.vn/cinema/pee-nak-3-660",
//                "https://momo.vn/cinema/eternals-573",
//                "https://momo.vn/cinema/godzilla-vs-kong-87",
//                "https://momo.vn/cinema/everything-everywhere-all-at-once-772",
//                "https://momo.vn/cinema/the-black-phone-672",
//                "https://momo.vn/cinema/turning-red-634",
//                "https://momo.vn/cinema/ivanna-813",
//                "https://momo.vn/cinema/senior-playboy-junior-papa-776",
//                "https://momo.vn/cinema/hanh-phuc-mau-876",
//                "https://momo.vn/cinema/65-946",
//                "https://momo.vn/cinema/sing-2-602",
//                "https://momo.vn/cinema/the-lake-826",
//                "https://momo.vn/cinema/encanto-609",
//                "https://momo.vn/cinema/avatar-812",
//                "https://momo.vn/cinema/13-exorcismos-950",
//                "https://momo.vn/cinema/nhung-dua-tre-trong-suong-960",
//                "https://momo.vn/cinema/f9-75",
//                "https://momo.vn/cinema/tro-tan-ruc-ro-870",
//                "https://momo.vn/cinema/jailangkung-sandekala-872",
//                "https://momo.vn/cinema/death-on-the-nile-600",
//                "https://momo.vn/cinema/where-the-crawdads-sing-698",
//                "https://momo.vn/cinema/that-time-i-got-reincarnated-as-a-slime-scarlet-bond-943",
//                "https://momo.vn/cinema/inhuman-kiss-986",
//                "https://momo.vn/cinema/titanic-919",
//                "https://momo.vn/cinema/paws-of-fury-the-legend-of-hank-802",
//                "https://momo.vn/cinema/the-bad-guys-643",
//                "https://momo.vn/cinema/pulau-964",
//                "https://momo.vn/cinema/bearman-978",
//                "https://momo.vn/cinema/prey-for-the-devil-794",
//                "https://momo.vn/cinema/suga-road-to-dday-1009",
//                "https://momo.vn/cinema/communion-girl-959",
//                "https://momo.vn/cinema/vo-dien-sat-nhan-810",
//                "https://momo.vn/cinema/ambulance-645",
//                "https://momo.vn/cinema/decibel-871",
//                "https://momo.vn/cinema/belle-ryu-to-sobakasu-no-hime-630",
//                "https://momo.vn/cinema/the-boogeyman-1005",
//                "https://momo.vn/cinema/my-beautiful-man-eternal-998",
//                "https://momo.vn/cinema/wrath-of-man-640",
//                "https://momo.vn/cinema/nope-635",
//                "https://momo.vn/cinema/trang-ti-phieu-luu-ky-567",
//                "https://momo.vn/cinema/sonic-the-hedgehog-2-653",
//                "https://momo.vn/cinema/the-ex-629"
//                "https://momo.vn/cinema/broker-702",
//                "https://momo.vn/cinema/the-lost-city-619",
//                "https://momo.vn/cinema/shangchi-and-the-legend-of-the-ten-rings-577",
//                "https://momo.vn/cinema/578-phat-dan-cua-ke-dien-72",
//                "https://momo.vn/cinema/lyle-lyle-crocodile-850",
//                "https://momo.vn/cinema/the-invitation-800",
//                "https://momo.vn/cinema/sao-the-movie-progressive-scherzo-of-deep-night-929",
//                "https://momo.vn/cinema/kamen-rider-geats-revice-movie-battle-royale-945",
//                "https://momo.vn/cinema/bed-rest-927",
//                "https://momo.vn/cinema/faces-of-anne-860",
//                "https://momo.vn/cinema/cracked-655",
//                "https://momo.vn/cinema/babylon-885",
//                "https://momo.vn/cinema/operation-fortune-ruse-de-guerre-910",
//                "https://momo.vn/cinema/to-the-solitary-me-that-loved-you-930",
//                "https://momo.vn/cinema/khanzab-999",
//                "https://momo.vn/cinema/cat-in-the-museum-987",
//                "https://momo.vn/cinema/pengabdi-setan-2-communion-823",
//                "https://momo.vn/cinema/quintessential-quintuplets-movie-840",
//                "https://momo.vn/cinema/the-policemans-lineage-627",
//                "https://momo.vn/cinema/tom-jerry-515",
//                "https://momo.vn/cinema/house-of-gucci-621",
//                "https://momo.vn/cinema/no-hard-feelings-983",
//                "https://momo.vn/cinema/my-beautiful-man-979",
//                "https://momo.vn/cinema/lightyear-681",
//                "https://momo.vn/cinema/knock-at-the-cabin-833",
//                "https://momo.vn/cinema/the-amazing-maurice-915",
//                "https://momo.vn/cinema/creed-iii-866",
//                "https://momo.vn/cinema/mal-de-ojo-852",
//                "https://momo.vn/cinema/the-woman-king-789",
//                "https://momo.vn/cinema/stand-by-me-2-590",
//                "https://momo.vn/cinema/mortal-kombat-85",
//                "https://momo.vn/cinema/the-point-man-937",
//                "https://momo.vn/cinema/winner-2022-the-circle-958",
//                "https://momo.vn/cinema/guimoon-the-lightless-door-607",
//                "https://momo.vn/cinema/plane-922",
//                "https://momo.vn/cinema/mummies-909",
//                "https://momo.vn/cinema/renfield-907",
//                "https://momo.vn/cinema/oh-my-girl-893",
//                "https://momo.vn/cinema/chickenhare-and-the-hamster-of-darkness-671",
//                "https://momo.vn/cinema/sword-art-online-the-movie-progressive-aria-of-a-starless-night-628",
//                "https://momo.vn/cinema/tid-noi-more-than-true-love-990",
//                "https://momo.vn/cinema/come-play-with-me-656",
//                "https://momo.vn/cinema/decision-to-leave-700",
//                "https://momo.vn/cinema/dc-league-of-superpets-787",
//                "https://momo.vn/cinema/confession-889",
//                "https://momo.vn/cinema/haunted-tales-678",
//                "https://momo.vn/cinema/resident-evil-welcome-to-raccoon-city-596",
//                "https://momo.vn/cinema/the-medium-574",
//                "https://momo.vn/cinema/fast-feel-love-670",
//                "https://momo.vn/cinema/the-matrix-resurrections-584",
//                "https://momo.vn/cinema/sky-tour-339",
//                "https://momo.vn/cinema/vanity-fair-617",
//                "https://momo.vn/cinema/honest-candidate-2-853",
//                "https://momo.vn/cinema/haunted-universities-2nd-semester-788",
//                "https://momo.vn/cinema/argonuts-948",
//                "https://momo.vn/cinema/to-every-you-ive-loved-before-931",
//                "https://momo.vn/cinema/my-heart-puppy-951",
//                "https://momo.vn/cinema/virus-cuong-loan-846",
//                "https://momo.vn/cinema/beast-807",
//                "https://momo.vn/cinema/dune-571",
//                "https://momo.vn/cinema/gintama-the-very-final-618",
//                "https://momo.vn/cinema/my-hero-academia-world-heroes-mission-604",
//                "https://momo.vn/cinema/happy-new-year-595",
//                "https://momo.vn/cinema/trinh-cong-son-697",
//                "https://momo.vn/cinema/midnight-661",
//                "https://momo.vn/cinema/fall-806",
//                "https://momo.vn/cinema/hunt-864",
//                "https://momo.vn/cinema/its-in-the-woods-926",
//                "https://momo.vn/cinema/the-ghost-within-974",
//                "https://momo.vn/cinema/rasuk-888",
//                "https://momo.vn/cinema/hansan-rising-dragon-814",
//                "https://momo.vn/cinema/jeepers-creepers-reborn-830",
//                "https://momo.vn/cinema/gaia-644",
//                "https://momo.vn/cinema/firestarter-677",
//                "https://momo.vn/cinema/nguoi-tinh-623",
//                "https://momo.vn/cinema/the-one-962",
//                "https://momo.vn/cinema/the-fabelmans-928",
//                "https://momo.vn/cinema/the-lord-of-the-rings-the-fellowship-of-the-ring-832",
//                "https://momo.vn/cinema/happy-ending-839",
//                "https://momo.vn/cinema/the-boss-baby-family-business-482",
//                "https://momo.vn/cinema/escape-from-mogadishu-786",
//                "https://momo.vn/cinema/scamsgiving-947",
//                "https://momo.vn/cinema/how-to-save-the-immortal-956",
//                "https://momo.vn/cinema/dont-worry-darling-821",
//                "https://momo.vn/cinema/detective-conan-the-scarlet-bullet-83",
//                "https://momo.vn/cinema/nguoi-lang-nghe-loi-thi-tham-625",
//                "https://momo.vn/cinema/vung-dat-cam-lang-2-397",
//                "https://momo.vn/cinema/the-anchor-683",
//                "https://momo.vn/cinema/harry-potter-and-the-philosophers-stone-687",
//                "https://momo.vn/cinema/nix-835",
//                "https://momo.vn/cinema/evangelion-30-10-thrice-upon-a-time-thrice-upon-a-time-828",
//                "https://momo.vn/cinema/amsterdam-784",
//                "https://momo.vn/cinema/the-other-child-861",
//                "https://momo.vn/cinema/the-creeping-1000",
//                "https://momo.vn/cinema/hypnotic-995"

        ));

        for (String url : urls) {
            crawlFilm(url);
        }
    }
}
