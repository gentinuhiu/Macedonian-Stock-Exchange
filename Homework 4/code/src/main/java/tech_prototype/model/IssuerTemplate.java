package tech_prototype.model;

public class IssuerTemplate {
    public String name;

    public IssuerTemplate(String name) {
        this.name = name.split("\\.")[0];
    }
}
