package net.manaty.octopusync;

import io.bootique.Bootique;

public class Main {

     public static void main(String[] args) {
         Bootique.app(args)
                 .module(new MainModuleProvider())
                 .module(new WebModuleProvider())
                 .exec().exit();
     }
}
