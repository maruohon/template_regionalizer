package regionalizer.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class TemplateSplitter
{
    private final File outputDir;
    private final File templateFile;

    public TemplateSplitter(File outputDir, File templateFile)
    {
        this.outputDir = outputDir;
        this.templateFile = templateFile;
    }

    public boolean splitTemplate(TemplateProperties props)
    {
        try
        {
            BufferedImage inputImage = ImageIO.read(this.templateFile);
            BufferedImage regionImage = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
            final int width = inputImage.getWidth();
            final int height = inputImage.getHeight();
            final int offsetX = props.getOffsetX();
            final int offsetZ = props.getOffsetZ();
            final int templateWorldStartX = props.getStartXForTemplateWidth(width);
            final int templateWorldStartZ = props.getStartZForTemplateHeight(height);
            final int templateWorldEndXExc = props.getEndXExclusiveForTemplateWidth(width);
            final int templateWorldEndZExc = props.getEndZExclusiveForTemplateHeight(height);

            final int totalAreaStartX = (templateWorldStartX & ~0x1FF);
            final int totalAreaStartZ = (templateWorldStartZ & ~0x1FF);
            final int totalAreaEndXExc = ((int) Math.ceil((double) templateWorldEndXExc / 512D)) << 9;
            final int totalAreaEndZExc = ((int) Math.ceil((double) templateWorldEndZExc / 512D)) << 9;
            final int totalAreaEndXInc = totalAreaEndXExc - 1;
            final int totalAreaEndZInc = totalAreaEndZExc - 1;

            System.out.printf("width: %d\n", width);
            System.out.printf("height: %d\n", height);
            System.out.printf("offsetX: %d\n", offsetX);
            System.out.printf("offsetZ: %d\n", offsetZ);
            System.out.printf("templateWorldStartX: %d\n", templateWorldStartX);
            System.out.printf("templateWorldStartZ: %d\n", templateWorldStartZ);
            System.out.printf("templateWorldEndXExc: %d\n", templateWorldEndXExc);
            System.out.printf("templateWorldEndZExc: %d\n", templateWorldEndZExc);
            System.out.printf("totalAreaStartX: %d\n", totalAreaStartX);
            System.out.printf("totalAreaStartZ: %d\n", totalAreaStartZ);
            System.out.printf("totalAreaEndXExc: %d\n", totalAreaEndXExc);
            System.out.printf("totalAreaEndZExc: %d\n", totalAreaEndZExc);
            System.out.printf("totalAreaEndXInc: %d\n", totalAreaEndXInc);
            System.out.printf("totalAreaEndZInc: %d\n", totalAreaEndZInc);

            for (int regionZ = (totalAreaStartZ >> 9); regionZ <= (totalAreaEndZInc >> 9); regionZ++)
            {
                final int startZ = (regionZ << 9);

                for (int regionX = (totalAreaStartX >> 9); regionX <= (totalAreaEndXInc >> 9); regionX++)
                {
                    System.out.printf("Region: r.%d.%d\n", regionX, regionZ);
                    final int startX = (regionX << 9);

                    File output = new File(this.outputDir, "r." + regionX + "." + regionZ + ".png");

                    if (props.getOverride() == false && output.exists())
                    {
                        System.out.printf("Template file '%s' already exists, skipping...\n", output.getPath());
                        continue;
                    }

                    int x = 0;
                    int z = 0;
                    int w = 0;
                    int h = 0;
                    int worldX = startX;
                    int worldZ = startZ;
                    int zeroes[] = new int[512 * 512];

                    // Horizontal area that is outside the input template image (above)
                    if (worldZ < templateWorldStartZ)
                    {
                        h = templateWorldStartZ - worldZ;
                        regionImage.setRGB(x, z, 512, h, zeroes, 0, 512);
                        worldZ += h;
                        z += h;
                    }

                    h = Math.min(512 - z, templateWorldEndZExc - worldZ);

                    // The area on the left side inside the first region template, that is outside the input template area
                    if (worldX < templateWorldStartX)
                    {
                        w = templateWorldStartX - worldX;
                        regionImage.setRGB(x, z, w, h, zeroes, 0, 512);
                        worldX += w;
                        x += w;
                        // Don't increment z yet
                    }

                    // Normal area that is inside the input template image
                    w = Math.min(512 - x, templateWorldEndXExc - worldX);
                    int[] data = new int[512 * 512];
                    inputImage.getRGB(worldX - templateWorldStartX, worldZ - templateWorldStartZ, w, h, data, 0, 512);
                    regionImage.setRGB(x, z, w, h, data, 0, 512);
                    x += w;

                    // The area on the right side inside the first region template, that is outside the input template area
                    if (x < 512)
                    {
                        w = 512 - x;
                        regionImage.setRGB(x, z, w, h, zeroes, 0, 512);
                        // No need to increment x anymore
                    }

                    z += h;

                    // Horizontal area that is outside the input template image (below)
                    if (z < 512)
                    {
                        h = 512 - z;
                        regionImage.setRGB(0, z, 512, h, zeroes, 0, 512);
                    }

                    ImageIO.write(regionImage, "png", output);
                }
            }
        }
        catch (IOException e)
        {
            System.err.printf("Image splitting failed!\n");
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
