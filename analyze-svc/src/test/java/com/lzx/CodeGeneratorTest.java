package com.lzx;

import com.lzx.util.MybatisPlusGeneratorUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CodeGeneratorTest {
    // Mysql
    @Test
    public void test() {
        String[] tableNames = {"dangerous_ip"};
        MybatisPlusGeneratorUtil generator = new MybatisPlusGeneratorUtil(
                "Bobby.zx.lin",
                "com.lzx",
                "jdbc:mysql://localhost:3306/ipdb",
                "root",
                "123456"
        );
        generator.startGenerator(tableNames);
    }
}
