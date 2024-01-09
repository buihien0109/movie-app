package com.example.movieapp.utils.crawl;

import com.example.movieapp.entity.*;
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
            Random random = new Random();
            Document doc = Jsoup.connect(url).get();

            String title = doc.selectFirst(".jsx-9e4ccf1f4860abb8.mt-2.text-2xl.font-bold.text-white").text();
            String slug = slugify.slugify(title);
            String description = doc.selectFirst(".jsx-9e4ccf1f4860abb8.mt-1.text-sm.leading-relaxed.text-white.text-opacity-70").text();
            String poster = doc.selectFirst("meta[property=og:image]").attr("content");

            log.info("Title: {}", title);
            log.info("Slug: {}", slug);
            log.info("Description: {}", description);
            log.info("Poster: {}", poster);

            // Random year from 2021 to 2023
            Integer releaseYear = random.nextInt(3) + 2021;
            log.info("Release year: {}", releaseYear);

            // Random view from 1000 to 9000
            Integer view = random.nextInt(8000) + 1000;
            log.info("View: {}", view);

            // Random rating from 5 to 10
            Double rating = random.nextInt(5) + 5.0;
            log.info("Rating: {}", rating);

            // Random type
            FilmType type = FilmType.values()[random.nextInt(FilmType.values().length)];
            log.info("Type: {}", type);

            // Random status
            Boolean status = true;

            // Lấy danh sách thể loại
            Element genreElements = doc.select(".jsx-9e4ccf1f4860abb8.mt-1.font-bold.text-white.text-opacity-90").get(1);
            Set<Genre> genreList = parseGenre(genreElements);
            log.info("Genre: {}", genreList);

            // Lấy danh sách đạo diễn
            Elements people = doc.select(".jsx-1515330669.actor-col");

            List<Director> directorList = new ArrayList<>();
            if (people != null && !people.isEmpty()) {
                Element directorElement = people.get(0);
                Director director = parseDirector(directorElement);
                directorList.add(director);
                log.info("Director: {}", director);
            }


            // Lấy danh sách diễn viên
            List<Actor> actorList = new ArrayList<>();
            if (people != null && people.size() > 1) {
                List<Element> actorElements = people.subList(1, people.size());
                actorList = parseActor(actorElements);
                log.info("Actor List");
                actorList.forEach(actor -> log.info("Actor: {}", actor));
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
                "https://momo.vn/cinema/semantic-error-825",
                "https://momo.vn/cinema/morbius-624",
                "https://momo.vn/cinema/lat-mat-48h-88",
                "https://momo.vn/cinema/biet-doi-rat-on-955",
                "https://momo.vn/cinema/dont-look-at-the-demon-847",
                "https://momo.vn/cinema/m3gan-842",
                "https://momo.vn/cinema/top-gun-maverick-676",
                "https://momo.vn/cinema/men-gai-mien-tay-657",
                "https://momo.vn/cinema/kisaragi-station-820",
                "https://momo.vn/cinema/the-super-mario-bros-movie-884",
                "https://momo.vn/cinema/dungeons-dragons-honor-among-thieves-980",
                "https://momo.vn/cinema/hoon-payon-1003",
                "https://momo.vn/cinema/the-witch-part2-the-other-one-701",
                "https://momo.vn/cinema/pee-nak-3-660",
                "https://momo.vn/cinema/eternals-573",
                "https://momo.vn/cinema/godzilla-vs-kong-87",
                "https://momo.vn/cinema/everything-everywhere-all-at-once-772",
                "https://momo.vn/cinema/the-black-phone-672",
                "https://momo.vn/cinema/turning-red-634",
                "https://momo.vn/cinema/ivanna-813",
                "https://momo.vn/cinema/senior-playboy-junior-papa-776",
                "https://momo.vn/cinema/hanh-phuc-mau-876",
                "https://momo.vn/cinema/65-946",
                "https://momo.vn/cinema/sing-2-602",
                "https://momo.vn/cinema/the-lake-826",
                "https://momo.vn/cinema/encanto-609",
                "https://momo.vn/cinema/avatar-812",
                "https://momo.vn/cinema/13-exorcismos-950",
                "https://momo.vn/cinema/nhung-dua-tre-trong-suong-960",
                "https://momo.vn/cinema/f9-75",
                "https://momo.vn/cinema/tro-tan-ruc-ro-870",
                "https://momo.vn/cinema/jailangkung-sandekala-872",
                "https://momo.vn/cinema/death-on-the-nile-600",
                "https://momo.vn/cinema/where-the-crawdads-sing-698",
                "https://momo.vn/cinema/that-time-i-got-reincarnated-as-a-slime-scarlet-bond-943",
                "https://momo.vn/cinema/inhuman-kiss-986",
                "https://momo.vn/cinema/titanic-919",
                "https://momo.vn/cinema/paws-of-fury-the-legend-of-hank-802",
                "https://momo.vn/cinema/the-bad-guys-643",
                "https://momo.vn/cinema/pulau-964",
                "https://momo.vn/cinema/bearman-978",
                "https://momo.vn/cinema/prey-for-the-devil-794",
                "https://momo.vn/cinema/suga-road-to-dday-1009",
                "https://momo.vn/cinema/communion-girl-959",
                "https://momo.vn/cinema/vo-dien-sat-nhan-810",
                "https://momo.vn/cinema/ambulance-645",
                "https://momo.vn/cinema/decibel-871",
                "https://momo.vn/cinema/belle-ryu-to-sobakasu-no-hime-630",
                "https://momo.vn/cinema/the-boogeyman-1005",
                "https://momo.vn/cinema/my-beautiful-man-eternal-998",
                "https://momo.vn/cinema/wrath-of-man-640",
                "https://momo.vn/cinema/nope-635",
                "https://momo.vn/cinema/trang-ti-phieu-luu-ky-567",
                "https://momo.vn/cinema/sonic-the-hedgehog-2-653",
                "https://momo.vn/cinema/the-ex-629"
        ));

        for (String url : urls) {
            crawlFilm(url);
        }
    }
}
