package com.avalon.base.gui.theme;

import net.minecraft.Util;

/**
 * 简单的一阶缓动动画工具，用于控件 hover / 切换动画。
 */
public final class Anim {
    private float value, target;
    private long lastTime = Util.getMillis();

    public Anim(float initial) {
        this.value = this.target = initial;
    }

    public void setTarget(float t) {
        this.target = t;
    }

    public void snap(float v) {
        this.value = this.target = v;
    }

    public float target() {
        return target;
    }

    public float value() {
        return value;
    }

    public float tick(float speed) {
        long now = Util.getMillis();
        float dt = Math.min(0.1f, (now - lastTime) / 1000f);
        lastTime = now;
        value += (target - value) * Math.min(1f, speed * dt);
        if (Math.abs(target - value) < 0.002f) value = target;
        return value;
    }
}
