package me.syari.ss.discord.internal.utils.tuple;

import java.io.Serializable;
import java.util.Objects;


public final class Pair<L, R> implements Serializable {
    public final L left;

    public final R right;

    public Pair(final L left, final R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }


    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Pair<?, ?>) {
            final Pair<?, ?> other = (Pair<?, ?>) obj;
            return Objects.equals(getLeft(), other.getLeft())
                    && Objects.equals(getRight(), other.getRight());
        }
        return false;
    }


    @Override
    public int hashCode() {

        return Objects.hashCode(getLeft()) ^ Objects.hashCode(getRight());
    }


    @Override
    public String toString() {
        return "(" + getLeft() + ',' + getRight() + ')';
    }

}
