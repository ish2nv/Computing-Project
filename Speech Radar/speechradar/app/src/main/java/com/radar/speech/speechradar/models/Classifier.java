package com.radar.speech.speechradar.models;

public interface Classifier {
    String name();

    Classification recognize(final float[] pixels);
}
