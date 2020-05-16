package me.syari.ss.discord.internal.utils.tuple;

import java.io.Serializable;
import java.util.Objects;


public abstract class Pair<L, R> implements Serializable {


    public static <L, R> Pair<L, R> of(final L left, final R right) {
        return new ImmutablePair<>(left, right);
    }


    public abstract L getLeft();


    public abstract R getRight();


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
