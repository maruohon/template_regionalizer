package regionalizer;

import java.io.File;
import regionalizer.image.TemplateProperties;
import regionalizer.image.TemplateProperties.Alignment;
import regionalizer.image.TemplateSplitter;

public class Regionalizer
{
    private static File outputDir = new File(".");
    private static File templateFile = null;

    public static void main(String[] args)
    {
        TemplateProperties props = new TemplateProperties();

        if (handleArguments(args, props))
        {
            System.out.printf("Running with the following options:\n");
            System.out.printf("  --align-mode=%s\n", props.getAlignment().toString());
            System.out.printf("  --align-offset=%d,%d\n", props.getOffsetX(), props.getOffsetZ());
            System.out.printf("  --out-dir=%s\n", outputDir.getPath());

            if (props.getOverride())
                System.out.printf("  --override\n");

            System.out.printf("  template input file: '%s'\n\n", templateFile.getPath());

            TemplateSplitter splitter = new TemplateSplitter(outputDir, templateFile);

            if (splitter.splitTemplate(props))
            {
                System.out.printf("Successfully split the image '%s'\n", templateFile.getPath());
            }
            else
            {
                System.err.printf("Failed to split the image '%s'\n", templateFile.getPath());
            }
        }
    }

    private static boolean handleArguments(String[] args, TemplateProperties props)
    {
        if (args.length <= 0)
        {
            printUsage();
            return false;
        }

        for (int i = 0; i < args.length; i++)
        {
            final String arg = args[i];

            if (arg.startsWith("--align-mode=") && arg.length() > 13)
            {
                String val = arg.substring(13);

                if (val.equals("center"))
                {
                    props.setAlignment(Alignment.CENTER);
                }
                else if (val.equals("top-left"))
                {
                    props.setAlignment(Alignment.TOP_LEFT);
                }
                else
                {
                    System.err.printf("Invalig alignment mode: '%s'. Valid values: 'center', 'top-left'\n", val);
                    return false;
                }
            }
            else if (arg.startsWith("--align-offset=") && arg.length() > 15)
            {
                String val = arg.substring(15);
                int commaPos = val.indexOf(",");

                if (commaPos > 0 && val.length() > (commaPos + 1))
                {
                    try
                    {
                        int x = Integer.parseInt(val.substring(0, commaPos));
                        int z = Integer.parseInt(val.substring(commaPos + 1, val.length()));
                        props.setOffsetX(x).setOffsetZ(z);
                    }
                    catch (NumberFormatException e)
                    {
                        System.err.printf("Invalig alignment offset value: '%s'.\n" +
                                          "The valid format is --align-offset=123,456 where 123 " +
                                          "is the X coordinate and 456 the Z coordinate.\n", val);
                        return false;
                    }
                }
                else
                {
                    System.err.printf("Invalig alignment offset value: '%s'.\n" +
                                      "The valid format is --align-offset=123,456 where 123 " +
                                      "is the X coordinate and 456 the Z coordinate.\n", val);
                    return false;
                }
            }
            else if (arg.startsWith("--out-dir=") && arg.length() > 10)
            {
                String val = arg.substring(10);
                outputDir = new File(val);

                if (outputDir.exists() == false || outputDir.isDirectory() == false)
                {
                    System.err.printf("The output directory '%s' does not exist or is not a directory\n", val);
                    return false;
                }
            }
            else if (arg.equals("--override"))
            {
                props.setOverride(true);
            }
            else
            {
                if (templateFile != null)
                {
                    System.err.printf("Invalid arguments - tried to set the template file to '%s', " + 
                                      "but it had already been set to '%s'\n", arg, templateFile.getPath());
                    return false;
                }

                templateFile = new File(arg);

                if (templateFile.exists() == false || templateFile.isFile() == false)
                {
                    System.err.printf("The input template file '%s' does not exist or is not a regular file\n", arg);
                    return false;
                }
            }
        }

        if (templateFile == null)
        {
            System.err.printf("No input template file specified!\n\n");
            printUsage();
            return false;
        }

        return true;
    }

    private static void printUsage()
    {
        System.out.printf("Usage: java -jar regionalizer.jar [options] <input template file>\n");
        System.out.printf("Available options:\n");
        System.out.printf("  --align-mode=<center | top-left>\tSets the alignment, ie. which point\n" +
                          "\t\t\t\t\tof the template will be at the '--align-offset=x,z' coordinates\n");
        System.out.printf("  --align-offset=<x>,<z>\t\tSets the location of the template image in the world\n");
        System.out.printf("  --out-dir=path/to/output/\t\tThe directory where the per-region template images will be written to\n");
        System.out.printf("  --override\t\t\t\tIf given, then existing per-region template images will be overridden\n");
    }
}
