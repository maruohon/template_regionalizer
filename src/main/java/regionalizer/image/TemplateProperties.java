package regionalizer.image;

public class TemplateProperties
{
    private Alignment alignment = Alignment.CENTER;
    private int offsetX;
    private int offsetZ;
    private boolean override;

    public int getOffsetX()
    {
        return this.offsetX;
    }

    public int getOffsetZ()
    {
        return this.offsetZ;
    }

    public boolean getOverride()
    {
        return this.override;
    }

    public Alignment getAlignment()
    {
        return this.alignment;
    }

    public TemplateProperties setOffsetX(int offsetX)
    {
        this.offsetX = offsetX;
        return this;
    }

    public TemplateProperties setOffsetZ(int offsetZ)
    {
        this.offsetZ = offsetZ;
        return this;
    }

    public TemplateProperties setAlignment(Alignment alignment)
    {
        this.alignment = alignment;
        return this;
    }

    public TemplateProperties setOverride(boolean override)
    {
        this.override = override;
        return this;
    }

    /**
     * Gets the template area's start X coordinate, for a template of the given width
     * @param width
     * @return
     */
    public int getStartXForTemplateWidth(int width)
    {
        switch (this.alignment)
        {
            case TOP_LEFT:
                return this.offsetX;

            case CENTER:
            default:
                return this.offsetX - (width / 2);
        }
    }

    /**
     * Gets the template area's start X coordinate, for a template of the given width
     * @param width
     * @return
     */
    public int getStartZForTemplateHeight(int height)
    {
        switch (this.alignment)
        {
            case TOP_LEFT:
                return this.offsetZ;

            case CENTER:
            default:
                return this.offsetZ - (height / 2);
        }
    }

    /**
     * Gets the template area's end X coordinate, for a template of the given width.<br>
     * NOTE: This value is exclusive (ie. end + 1)
     * @param width
     * @return
     */
    public int getEndXExclusiveForTemplateWidth(int width)
    {
        return this.getStartXForTemplateWidth(width) + width;
    }

    /**
     * Gets the template area's end X coordinate, for a template of the given width.<br>
     * NOTE: This value is exclusive (ie. end + 1)
     * @param width
     * @return
     */
    public int getEndZExclusiveForTemplateHeight(int height)
    {
        return this.getStartZForTemplateHeight(height) + height;
    }

    public enum Alignment
    {
        CENTER,
        TOP_LEFT;

        @Override
        public String toString()
        {
            switch (this)
            {
                case TOP_LEFT:
                    return "top-left";
                case CENTER:
                default:
                    return "center";
            }
        };
    }
}
