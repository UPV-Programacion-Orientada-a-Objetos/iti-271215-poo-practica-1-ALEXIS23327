package edu.upvictoria.fpoo.Exceptions;

import java.io.FileNotFoundException;

public class FileDoesntExist extends FileNotFoundException {
    public FileDoesntExist(String mensaje){
        super(mensaje);
    }
}
