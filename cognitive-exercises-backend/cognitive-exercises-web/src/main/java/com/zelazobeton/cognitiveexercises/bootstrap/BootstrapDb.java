package com.zelazobeton.cognitiveexercises.bootstrap;

import static com.zelazobeton.cognitiveexercises.constant.FileConstants.EXAMPLE_USERNAMES_FILE;
import static com.zelazobeton.cognitiveexercises.constant.FileConstants.FORWARD_SLASH;
import static com.zelazobeton.cognitiveexercises.constant.FileConstants.MEMORY_IMG_FOLDER;
import static com.zelazobeton.cognitiveexercises.constant.FileConstants.MEMORY_IMG_PATH;
import static com.zelazobeton.cognitiveexercises.constant.FileConstants.VERSION_1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.zelazobeton.cognitiveexercises.domain.GameData;
import com.zelazobeton.cognitiveexercises.domain.Portfolio;
import com.zelazobeton.cognitiveexercises.domain.memory.MemoryImg;
import com.zelazobeton.cognitiveexercises.domain.User;
import com.zelazobeton.cognitiveexercises.repository.GameDataRepository;
import com.zelazobeton.cognitiveexercises.repository.MemoryImgRepository;
import com.zelazobeton.cognitiveexercises.repository.UserRepository;
import com.zelazobeton.cognitiveexercises.service.PortfolioBuilder;
import com.zelazobeton.cognitiveexercises.service.ResourceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile({"dev-mysql-bootstrap", "dev-oracle-bootstrap"})
public class BootstrapDb implements CommandLineRunner {
    @Value("${server-host}")
    private String serverHost;
    @Value("${server.port}")
    private String serverPort;
    private String serverAddress = this.serverHost + this.serverPort;
    private final GameDataRepository gameDataRepository;
    private final MemoryImgRepository memoryImgRepository;
    private final UserRepository userRepository;
    private final Random rand = new Random();
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ResourceService resourceService;
    private final PortfolioBuilder portfolioBuilder;

    @Override
    public void run(String... args) {
        this.loadMemoryImages();
        this.loadExampleUsers();
        this.loadGamesData();
    }

    private void loadGamesData() {
        this.gameDataRepository.save(GameData.builder()
                .title("Memory")
                .icon(this.serverAddress + VERSION_1 + FORWARD_SLASH + "games/icon/memory-icon.png")
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
                        .authServerId(UUID.randomUUID().toString())
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
        Portfolio portfolio = this.portfolioBuilder.createBootstrapPortfolioWithGeneratedAvatar(newUser);
        long score = this.rand.nextInt(1000);
        portfolio.setTotalScore(score);
    }

    private void loadMemoryImages() {
        Path memoryImagesFolder = this.resourceService.getPath(MEMORY_IMG_FOLDER);
        List<MemoryImg> memoryImgs = new ArrayList<>();
        for(File file: Objects.requireNonNull(memoryImagesFolder.toFile().listFiles())) {
            if (!file.isDirectory()) {
                String imgAddress = this.serverAddress + VERSION_1 + MEMORY_IMG_PATH + file.getName();
                memoryImgs.add(MemoryImg.builder().address(imgAddress).build());
            }
        }
        this.memoryImgRepository.saveAll(memoryImgs);
    }

}
