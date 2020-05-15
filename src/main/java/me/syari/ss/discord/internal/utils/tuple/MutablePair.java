

package me.syari.ss.discord.internal.utils.tuple;


public class MutablePair<L, R> extends Pair<L, R>
{

    public L left;

    public R right;


    public static <L, R> MutablePair<L, R> of(final L left, final R right) {
        return new MutablePair<>(left, right);
    }


    public MutablePair() {
        super();
    }


    public MutablePair(final L left, final R right) {
        super();
        this.left = left;
        this.right = right;
    }

    @Override
    public L getLeft() {
        return left;
    }


    public void setLeft(final L left) {
        this.left = left;
    }

    @Override
    public R getRight() {
        return right;
    }


    public void setRight(final R right) {
        this.right = right;
    }
}

