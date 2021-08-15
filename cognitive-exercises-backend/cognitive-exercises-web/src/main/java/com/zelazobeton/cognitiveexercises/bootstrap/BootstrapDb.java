package com.zelazobeton.cognitiveexercises.bootstrap;

import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.EXAMPLE_USERNAMES_FILE;
import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.FORWARD_SLASH;
import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.LOCALHOST_ADDRESS;
import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.MEMORY_IMG_FOLDER;
import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.MEMORY_IMG_PATH;
import static com.zelazobeton.cognitiveexercieses.constant.FileConstants.VERSION_1;
import static com.zelazobeton.cognitiveexercieses.constant.RolesConstant.ADMIN;
import static com.zelazobeton.cognitiveexercieses.constant.RolesConstant.USER;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.zelazobeton.cognitiveexercieses.domain.GameData;
import com.zelazobeton.cognitiveexercieses.domain.Portfolio;
import com.zelazobeton.cognitiveexercieses.domain.memory.MemoryImg;
import com.zelazobeton.cognitiveexercieses.domain.security.Authority;
import com.zelazobeton.cognitiveexercieses.domain.security.Role;
import com.zelazobeton.cognitiveexercieses.domain.security.User;
import com.zelazobeton.cognitiveexercieses.repository.AuthorityRepository;
import com.zelazobeton.cognitiveexercieses.repository.GameDataRepository;
import com.zelazobeton.cognitiveexercieses.repository.MemoryImgRepository;
import com.zelazobeton.cognitiveexercieses.repository.RoleRepository;
import com.zelazobeton.cognitiveexercieses.repository.UserRepository;
import com.zelazobeton.cognitiveexercieses.service.PortfolioBuilder;
import com.zelazobeton.cognitiveexercieses.service.ResourceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile({"dev-mysql-bootstrap"})
public class BootstrapDb implements CommandLineRunner {
    private final GameDataRepository gameDataRepository;
    private final AuthorityRepository authorityRepository;
    private final RoleRepository roleRepository;
    private final MemoryImgRepository memoryImgRepository;
    private final UserRepository userRepository;
    private final Random rand = new Random();
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ResourceService resourceService;
    private final PortfolioBuilder portfolioBuilder;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Override
    public void run(String... args) {
        this.loadRoles();
        this.loadMemoryImages();
        this.loadExampleUsers();
        this.loadGamesData();
    }

    private void loadGamesData() {
        this.gameDataRepository.save(GameData.builder()
                .title("Memory")
                .icon(LOCALHOST_ADDRESS + VERSION_1 + FORWARD_SLASH + "games/icon/memory-icon.png")
                .build());
    }

    void loadExampleUsers() {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        TypedQuery<Role> q = entityManager.createQuery(
                "SELECT a FROM Role a LEFT JOIN FETCH a.users WHERE a.name=:name", Role.class)
                .setParameter("name", USER);
        Role userRole = q.getSingleResult();

        entityManager.getTransaction().commit();
        entityManager.close();

        List<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(new ClassPathResource(EXAMPLE_USERNAMES_FILE).getPath()))) {
            User newUser;
            for(String line; (line = br.readLine()) != null; ) {
                newUser = User.builder()
                        .username(line)
                        .email(line + "@domain.com")
                        .password(this.bCryptPasswordEncoder.encode(line))
                        .role(userRole)
                        .build();
                userRole.addUser(newUser);
                this.generatePortfolio(newUser);
                users.add(newUser);
            }
        } catch (IOException ex) {
            log.info(ex.toString());
        }
        this.roleRepository.save(userRole);
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
                String imgAddress = LOCALHOST_ADDRESS + VERSION_1 + MEMORY_IMG_PATH + file.getName();
                memoryImgs.add(MemoryImg.builder().address(imgAddress).build());
            }
        }
        this.memoryImgRepository.saveAll(memoryImgs);
    }

    private void loadRoles() {
        Authority createUser = this.authorityRepository.save(Authority.builder().permission("user.create").build());
        Authority updateUser = this.authorityRepository.save(Authority.builder().permission("user.update").build());
        Authority readUser = this.authorityRepository.save(Authority.builder().permission("user.read").build());
        Authority deleteUser = this.authorityRepository.save(Authority.builder().permission("user.delete").build());

        Role adminRole = Role.builder().name(ADMIN).build();
        Role userRole = Role.builder().name(USER).build();

        adminRole.setAuthorities(Set.of(createUser, updateUser, readUser, deleteUser));
        userRole.setAuthorities(Set.of(readUser, updateUser, deleteUser));
        this.roleRepository.saveAll(Arrays.asList(adminRole, userRole));

        log.debug("Authorities Loaded: " + this.authorityRepository.count());
        log.debug("Roles Loaded: " + this.roleRepository.count());
    }
}
