package net.manaty.octopusync;

import io.bootique.Bootique;
import net.manaty.octopusync.di.MainModule;

public class Main {

     public static void main(String[] args) {
         Bootique.app(args).autoLoadModules().module(new MainModule()).exec().exit();
     }
}
