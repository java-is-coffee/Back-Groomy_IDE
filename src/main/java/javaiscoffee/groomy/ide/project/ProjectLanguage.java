package javaiscoffee.groomy.ide.project;

public enum ProjectLanguage {
    JAVA("openjdk:11"),
    JAVASCRIPT("node:latest"),
    PYTHON("python:3"),
    CPP("gcc:latest"),
    C("gcc:latest"),
    KOTLIN("openjdk:17");

    private final String image;

    ProjectLanguage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }
}
