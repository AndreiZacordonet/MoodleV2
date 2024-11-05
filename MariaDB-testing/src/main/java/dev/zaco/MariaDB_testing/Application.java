package dev.zaco.MariaDB_testing;

import dev.zaco.MariaDB_testing.models.Achievement;
import dev.zaco.MariaDB_testing.models.Game;
import dev.zaco.MariaDB_testing.models.User;
import dev.zaco.MariaDB_testing.repositories.AchievementRepository;
import dev.zaco.MariaDB_testing.repositories.GameRepository;
import dev.zaco.MariaDB_testing.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

	public final UserRepository userRepository;
	public final GameRepository gameRepository;
	public final AchievementRepository achievementRepository;

    public Application(UserRepository userRepository, GameRepository gameRepository, AchievementRepository achievementRepository) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.achievementRepository = achievementRepository;
    }

    public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Game chess = new Game();
		chess.setName("Chess");
		chess.setYearOfPublishing(2000);
		gameRepository.save(chess);

		Game monopoly = new Game();
		monopoly.setName("Monopoly");
		monopoly.setYearOfPublishing(1999);
		gameRepository.save(monopoly);

		Achievement win10 = new Achievement();
		win10.setName("Won 10 games");
		win10.setGame(chess);
		achievementRepository.save(win10);

		Achievement win5 = new Achievement();
		win5.setName("Won 5 games");
		win5.setGame(monopoly);
		achievementRepository.save(win5);

		User john = new User();
		john.setUsername("John");
		john.getGames().add(chess);
		userRepository.save(john);

		User jane = new User();
		jane.setUsername("Jane");
		jane.getGames().add(monopoly);
		userRepository.save(jane);
		for (User user : userRepository.findAll()) {
			System.out.printf("User: %s\n", user.getUsername());
			for (Game game: user.getGames()) {
				System.out.printf("\t->Game: %s\n", game.getName());
				for (Achievement achievement : game.getAchievements()) {
					System.out.printf("\t->Achievement: %s\n", achievement.getName());
				}
			}
		}
	}

}
