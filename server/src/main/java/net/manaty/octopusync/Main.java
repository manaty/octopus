package net.manaty.octopusync;

import io.bootique.Bootique;
import io.bootique.liquibase.LiquibaseModuleProvider;

public class Main {

     public static void main(String[] args) {
         Bootique.app(args)
                 .module(new LiquibaseModuleProvider())
                 .module(new MainModuleProvider())
                 .module(new WebModuleProvider())
                 .exec().exit();
     }
}
