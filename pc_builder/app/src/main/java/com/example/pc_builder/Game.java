package com.example.pc_builder;

public class Game {
    private int id;
    private String GameName;
    private int CpuScore;
    private int GpuScore;
    private int Ram;
    private String ImagePath;

    public Game(int id,String GameName,int CpuScore,int GpuScore,int Ram,String ImagePath){
        this.id = id;
        this.GameName = GameName;
        this.CpuScore = CpuScore;
        this.GpuScore = GpuScore;
        this.Ram = Ram;
        this.ImagePath = ImagePath;
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return GameName;
    }

    public String getImagePath(){
        return ImagePath;
    }
}
