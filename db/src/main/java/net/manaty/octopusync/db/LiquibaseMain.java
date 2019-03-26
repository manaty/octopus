package net.manaty.octopusync.db;

import io.bootique.Bootique;

public class LiquibaseMain {

    public static void main(String[] args) {
        Bootique.app(args).autoLoadModules().exec().exit();
    }
}
