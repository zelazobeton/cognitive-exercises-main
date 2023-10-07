package com.zelazobeton.cognitiveexercises.bootstrap;

import static com.zelazobeton.cognitiveexercises.shared.FileConstants.EXAMPLE_USERNAMES_FILE;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.zelazobeton.cognitiveexercises.games.application.GameDataRepository;
import com.zelazobeton.cognitiveexercises.games.domain.GameData;
import com.zelazobeton.cognitiveexercises.user.application.UserRepository;
import com.zelazobeton.cognitiveexercises.user.domain.Portfolio;
import com.zelazobeton.cognitiveexercises.user.domain.PortfolioBuilder;
import com.zelazobeton.cognitiveexercises.user.domain.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile({"dev-mysql-bootstrap", "dev-oracle-bootstrap", "dev-h2"})
public class BootstrapDb implements CommandLineRunner {
    private final UserRepository userRepository;
    private final GameDataRepository gameDataRepository;
    private final Random rand = new Random();
    private final PortfolioBuilder portfolioBuilder;
    @Value("${frontend-address}")
    private String frontendAddress;

    @Override
    public void run(String... args) {
        this.loadExampleUsers();
        this.loadGamesData();
    }

    private void loadGamesData() {
        this.gameDataRepository.save(GameData.builder()
                .title("Memory")
                .icon(this.frontendAddress + "/main/games/v1/icon/memory-icon.png")
                .build());
    }

    private void loadExampleUsers() {

        List<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(new ClassPathResource(EXAMPLE_USERNAMES_FILE).getPath()))) {
            User newUser;
            for(String line; (line = br.readLine()) != null; ) {
                newUser = User.builder()
                        .username(line)
                        .email(line + "@domain.com")
                        .externalId(UUID.randomUUID().toString())
                        .build();
                this.generatePortfolio(newUser);
                users.add(newUser);
            }
        } catch (IOException ex) {
            log.info(ex.toString());
        }
        this.userRepository.saveAll(users);
    }

    private void generatePortfolio(User newUser) throws IOException{
        Portfolio portfolio = this.portfolioBuilder.createPortfolioWithGeneratedAvatar(newUser);
        long score = this.rand.nextInt(1000);
        portfolio.setTotalScore(score);
    }
}
