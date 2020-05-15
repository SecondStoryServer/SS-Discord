

package net.dv8tion.jda.internal.utils.tuple;

public class MutableTriple<LEFT, MIDDLE, RIGHT> extends MutablePair<LEFT, RIGHT>
{
    // public because it is also public in pair
    public MIDDLE middle;

    private MutableTriple(LEFT left, MIDDLE middle, RIGHT right)
    {
        super(left, right);
        this.middle = middle;
    }

    public static <LEFT, MIDDLE, RIGHT> MutableTriple<LEFT, MIDDLE, RIGHT> of(LEFT left, MIDDLE middle, RIGHT right)
    {
        return new MutableTriple<>(left, middle, right);
    }

    public MIDDLE getMiddle()
    {
        return middle;
    }

    public void setMiddle(MIDDLE middle)
    {
        this.middle = middle;
    }
}
