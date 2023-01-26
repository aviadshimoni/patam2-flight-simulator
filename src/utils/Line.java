package utils;

public record Line(float a, float b) {

    public float f(float x) {
        return a * x + b;
    }
}
