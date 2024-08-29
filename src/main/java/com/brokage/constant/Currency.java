package com.brokage.constant;

public enum Currency {
    TRY(1.0f);

    private float ratio;

    Currency(float ratio) {
        this.ratio = ratio;
    }

    public float getRatio() {
        return ratio;
    }
}
