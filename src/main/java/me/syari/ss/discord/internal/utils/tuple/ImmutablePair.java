package me.syari.ss.discord.internal.utils.tuple;


public final class ImmutablePair<L, R> extends Pair<L, R> {

    public final L left;

    public final R right;


    public ImmutablePair(final L left, final R right) {
        super();
        this.left = left;
        this.right = right;
    }

    @Override
    public L getLeft() {
        return left;
    }

    @Override
    public R getRight() {
        return right;
    }
}
