package com.example.movieapp;

import com.example.movieapp.entity.*;
import com.example.movieapp.model.enums.FilmType;
import com.example.movieapp.model.enums.UserRole;
import com.example.movieapp.repository.*;
import com.github.javafaker.Faker;
import com.github.slugify.Slugify;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

@SpringBootTest
class MovieAppApplicationTests {
    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private DirectorRepository directorRepository;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private Slugify slugify;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private EpisodeRepository episodeRepository;

    @Test
    void save_genres() {
        List<String> genres = Arrays.asList("Hành động", "Kinh dị", "Tình cảm", "Hài hước", "Viễn tưởng", "Phiêu lưu", "Tâm lý", "Hoạt hình", "Chiến tranh", "Thần thoại");
        for (String s : genres) {
            Genre genre = Genre.builder()
                    .name(s)
                    .slug(slugify.slugify(s))
                    .build();
            genreRepository.save(genre);
        }
    }

    @Test
    void save_directors() {
        Faker faker = new Faker();
        for (int i = 0; i < 20; i++) {
            String name = faker.name().fullName();
            Director director = Director.builder()
                    .name(name)
                    .description(faker.lorem().paragraph())
                    .avatar(generateLinkImage(name))
                    .birthday(faker.date().birthday())
                    .build();
            directorRepository.save(director);
        }
    }

    @Test
    void save_actors() {
        Faker faker = new Faker();
        for (int i = 0; i < 50; i++) {
            String name = faker.name().fullName();
            Actor actor = Actor.builder()
                    .name(name)
                    .description(faker.lorem().paragraph())
                    .avatar(generateLinkImage(name))
                    .birthday(faker.date().birthday())
                    .build();
            actorRepository.save(actor);
        }
    }

    @Test
    void save_films() {
        Faker faker = new Faker();
        Random random = new Random();
        List<Director> directors = directorRepository.findAll();
        List<Actor> actors = actorRepository.findAll();
        List<Genre> genres = genreRepository.findAll();

        for (int i = 0; i < 100; i++) {
            // create list director from 1 to 4
            Set<Director> directorSet = new LinkedHashSet<>();
            for (int j = 0; j < random.nextInt(4) + 1; j++) {
                directorSet.add(directors.get(random.nextInt(directors.size())));
            }

            // create list actor from 5 to 8
            Set<Actor> actorSet = new LinkedHashSet<>();
            for (int j = 0; j < random.nextInt(4) + 5; j++) {
                actorSet.add(actors.get(random.nextInt(actors.size())));
            }

            // create list genre from 1 to 5
            Set<Genre> genreSet = new LinkedHashSet<>();
            for (int j = 0; j < random.nextInt(5) + 1; j++) {
                genreSet.add(genres.get(random.nextInt(genres.size())));
            }

            String title = faker.name().fullName();
            Film film = Film.builder()
                    .title(title)
                    .slug(slugify.slugify(title))
                    .description(faker.lorem().paragraph())
                    .poster(generateLinkImage(title))
                    .releaseYear(faker.number().numberBetween(1990, 2021))
                    .view(faker.number().numberBetween(1000, 100000))
                    .rating(faker.number().randomDouble(1, 1, 10))
                    .type(FilmType.values()[faker.number().numberBetween(0, FilmType.values().length)])
                    .status(random.nextInt(2) == 0)
                    .directors(directorSet)
                    .actors(actorSet)
                    .genres(genreSet)
                    .build();
            filmRepository.save(film);
        }
    }

    @Test
    void save_users() {
        Faker faker = new Faker();
        for (int i = 0; i < 20; i++) {
            String name = faker.name().fullName();
            User user = User.builder()
                    .name(name)
                    .password(passwordEncoder.encode("123"))
                    .email(faker.internet().emailAddress())
                    .avatar(generateLinkImage(name))
                    .role(i == 0 || i == 1 ? UserRole.ADMIN : UserRole.USER)
                    .build();
            userRepository.save(user);
        }
    }

    @Test
    void save_reviews() {
        Faker faker = new Faker();
        Random random = new Random();
        List<Film> films = filmRepository.findAll();
        List<User> users = userRepository.findAll();

        for (Film film : films) {
            for (int j = 0; j < random.nextInt(20); j++) {
                Review review = Review.builder()
                        .user(users.get(random.nextInt(users.size())))
                        .comment(faker.lorem().paragraph())
                        .rating(faker.number().numberBetween(1, 10))
                        .film(film)
                        .build();
                reviewRepository.save(review);
            }
        }
    }

    @Test
    void save_videos() {
        List<Video> videos = videoRepository.findAll(PageRequest.of(0, 20, Sort.by("id"))).getContent();

        for (int i = 0; i < 100; i++) {
            // Lấy lần lượt video trong ds videos
            Video video = videos.get(i % videos.size());
            Video newVideo = Video.builder()
                    .name(video.getName())
                    .url(video.getUrl())
                    .size(video.getSize())
                    .duration(video.getDuration())
                    .build();
            videoRepository.save(newVideo);
        }
    }

    @Test
    void save_episode() {
        Random random = new Random();
        List<Film> films = filmRepository.findAll();
        List<Video> videos = videoRepository.findAll();
        int count = 0;

        for (Film film : films) {
            if(film.getType().equals(FilmType.PHIM_BO)) {
                for (int j = 0; j < random.nextInt(10) + 5; j++) {
                    // Get video by count
                    Video video = videos.get(count);
                    count++;

                    Episode episode = Episode.builder()
                            .film(film)
                            .title("Tập " + (j + 1))
                            .displayOrder(j + 1)
                            .video(video)
                            .status(true)
                            .build();
                    episodeRepository.save(episode);
                }
                filmRepository.save(film);
            } else {
                // Get video by count
                Video video = videos.get(count);
                count++;

                Episode episode = Episode.builder()
                        .film(film)
                        .title("Tập full")
                        .displayOrder(1)
                        .video(video)
                        .status(true)
                        .build();
                episodeRepository.save(episode);
            }
        }
    }

    @Test
    void save_blogs() {
        Faker faker = new Faker();
        Random rd = new Random();

        List<User> userList = userRepository.findByRole(UserRole.ADMIN);

        for (int i = 0; i < 10; i++) {
            String title = faker.book().title();
            Blog blog = Blog.builder()
                    .title(title)
                    .slug(slugify.slugify(title))
                    .description(faker.lorem().paragraph())
                    .content(faker.lorem().paragraph(100))
                    .status(rd.nextInt(2) == 0)
                    .user(userList.get(rd.nextInt(userList.size())))
                    .thumbnail(generateLinkImage(title))
                    .build();

            blogRepository.save(blog);
        }
    }

    // get character first each of word from string, and to uppercase
    private String getCharacter(String str) {
        String[] words = str.split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            result.append(word.charAt(0));
        }
        return result.toString().toUpperCase();
    }

    // generate link author avatar follow struct : https://placehold.co/200x200?text=[...]
    private String generateLinkImage(String name) {
        return "https://placehold.co/200x200?text=" + getCharacter(name);
    }
}
