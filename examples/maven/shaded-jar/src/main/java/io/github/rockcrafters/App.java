package io.github.rockcrafters;

import org.yaml.snakeyaml.Yaml;
/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        Yaml yaml = new Yaml();
        System.out.println("Hello World!" + yaml.load("Hello: World"));
    }
}
