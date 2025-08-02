package org.intenses.insanitymod.classChoose;

public class Pair<L, R> {
    private final L item;
    private final R damage;

    private Pair(L item, R damage) {
        this.item = item;
        this.damage = damage;
    }

    public static <L,R> Pair<L,R> of(L item, R damage) {
        return new Pair<>(item, damage);
    }

    public L getLeft() {
        return item;
    }

    public R getRight() {
        return damage;
    }
}