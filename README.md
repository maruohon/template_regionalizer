Template Regionalizer
======================
Template Regionalizer is a small utility program to split large images into 512x512 pixel images.
It is meant to be used with the Painted Biomes Minecraft mod.
It will take in one large PNG image, and split it into per-region template images, and name them
correctly following the Minecraft McRegion/Anvil region file naming, ready to be used by Painted Biomes.

Launch command
===============
`java -jar regionalizer.jar [options] <input template image>`

So for example:
`java -jar regionalizer.jar --align-offset=123,456 --out-dir=project_1 input_templates/project_1.png`

Available options
==================
* `--align-mode=<center | top-left>` - This will control how the template is aligned in the world. The default value is `center`.
* `--align-offset=123,456` - This will set the position in world where the selected alignment point (center or top-left corner) is set. The default value is `0,0`.
* `--out-dir=path/to/output/directory` - The directory wheree the split images will be written to.
* `--override` - If given, then existing images with the same names will be overridden.

Compiling
==========
* Run the command `./gradlew build`
* A compiled and executable jar `regionalizer.jar` will be placed inside `build/libs/`