package com.zCore.Render.Particles;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;

public class ParticleGenerator {

    public static int particleCount;
    public static int randX;
    public static int randY;

    public ArrayList<Particle> particles = new ArrayList();
    private Random random = new Random();
    private Timer timer = new Timer();

    public ParticleGenerator(int particleCount, int randX, int randY) {
        this.particleCount = particleCount;
        this.randX = randX;
        this.randY = randY;
        for (int i = 0; i < particleCount; i++) {
            this.particles.add(new Particle(this.random.nextInt(randX), this.random.nextInt(randY)));
        }
    }

    public void drawParticles() {
        for (Particle p : this.particles) {
            p.draw();
        }
    }
}