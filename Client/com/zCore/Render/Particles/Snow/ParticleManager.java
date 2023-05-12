package com.zCore.Render.Particles.Snow;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.client.gui.ScaledResolution;
import space.lunaclient.luna.impl.tools.extra.RenderHelper;

public class ParticleManager {
	private Particle particle;
	private int amount;
	public ArrayList<Particle> particles = new ArrayList();
	private Random random = new Random();
	private ScaledResolution res;

	public ParticleManager(Particle particle, int amount) {
		this.particle = particle;
		this.amount = amount;
		init();
	}

	private void init() {
		this.particles.clear();
		this.res = RenderHelper.getScaledRes();
		for (int i = 0; i < this.amount; i++) {
			ParticleSnow particle = new ParticleSnow();
			if ((particle instanceof ParticleSnow)) {
				particle = new ParticleSnow();
			}
			particle.vector.x = this.random.nextInt(res.getScaledWidth() + 1);
			particle.vector.y = this.random.nextInt(res.getScaledHeight() + 1);
			this.particles.add(particle);
		}
	}

	public void draw(int xAdd) {
		for (Particle particle : this.particles) {
			particle.draw(xAdd);
		}
	}
}
