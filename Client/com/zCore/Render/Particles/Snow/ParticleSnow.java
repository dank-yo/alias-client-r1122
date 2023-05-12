package com.zCore.Render.Particles.Snow;

import java.util.Random;

import com.zCore.Core.Colors;
import com.zCore.Core.zCore;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

public class ParticleSnow extends Particle {
	private Random random = new Random();
	private ScaledResolution res;

	public void draw(int xAdd) {
		prepare();

		move();

		drawPixel(xAdd);

		resetPos();
	}

	private void prepare() {
		this.res = space.lunaclient.luna.impl.tools.extra.RenderHelper.getScaledRes();
	}

	private void drawPixel(int xAdd) {
		float size = 10.0F;
		for (int i = 0; i < 10; i++) {
			int alpha = Math.min(0, 1 - i / 10);
			Gui.drawFilledCircle(this.vector.x, this.vector.y, size + (1.0F + i * 0.2F),
					zCore.reAlpha(Colors.WHITE.c, alpha));
		}
		Gui.drawFilledCircle(this.vector.x + xAdd, this.vector.y, 1.1F, zCore.reAlpha(-1, 0.2F));
		Gui.drawFilledCircle(this.vector.x + xAdd, this.vector.y, 0.8F, zCore.reAlpha(-1, 0.4F));
		Gui.drawFilledCircle(this.vector.x + xAdd, this.vector.y, 0.5F, zCore.reAlpha(-1, 0.6F));
		Gui.drawFilledCircle(this.vector.x + xAdd, this.vector.y, 0.3F, zCore.reAlpha(Colors.WHITE.c, 1.0F));
	}

	private void move() {
		float speed = 100.0F;
		this.vector.y += this.random.nextFloat() * 0.25F;
		this.vector.x -= this.random.nextFloat();
	}

	private void resetPos() {
		if (this.vector.x < 0.0F) {
			this.vector.x = this.res.getScaledWidth();
		}
		if (this.vector.y > this.res.getScaledHeight()) {
			this.vector.y = 0.0F;
		}
	}
}
