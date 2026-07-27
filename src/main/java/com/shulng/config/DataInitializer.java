package com.shulng.config;

import com.shulng.entity.Category;
import com.shulng.entity.User;
import com.shulng.repository.CategoryRepository;
import com.shulng.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, 
                           CategoryRepository categoryRepository, 
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        initAdminUser();
        initCategories();
    }

    private void initAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNickname("管理员");
            admin.setEmail("admin@lingtide.com");
            admin.setBio("系统管理员");
            admin.setRole(User.Role.ADMIN);
            admin.setStatus(1);
            userRepository.save(admin);
            System.out.println("========================================");
            System.out.println(" 管理员账号已创建 ");
            System.out.println(" 用户名: admin");
            System.out.println(" 密码: admin123");
            System.out.println("========================================");
        }
    }

    private void initCategories() {
        List<String[]> categories = Arrays.asList(
                new String[]{"影视", "电影、电视剧等影视内容"},
                new String[]{"动漫", "动画、漫画相关"},
                new String[]{"音乐", "音乐MV、演唱会等"},
                new String[]{"游戏", "游戏实况、攻略等"},
                new String[]{"知识", "教育、教程、讲座"},
                new String[]{"科技", "科技数码、硬件评测"},
                new String[]{"运动", "体育赛事、健身教学"},
                new String[]{"美食", "烹饪教程、美食分享"},
                new String[]{"旅行", "旅游风光、城市漫步"},
                new String[]{"生活", "日常分享、搞笑内容"}
        );

        for (String[] catData : categories) {
            String name = catData[0];
            if (categoryRepository.findAll().stream().noneMatch(c -> c.getName().equals(name))) {
                Category category = new Category();
                category.setName(name);
                category.setDescription(catData[1]);
                category.setSortOrder(0);
                category.setStatus(1);
                categoryRepository.save(category);
            }
        }
    }
}
