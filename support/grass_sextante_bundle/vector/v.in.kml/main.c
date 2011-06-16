/*
# FIRST CHECK WHAT THE OGR KML DRIVER IS CAPABLE OF!
# (import points and polygons from KML/KMZ files)
# - use expat (optional dependency of GRASS/OGR): http://www.xml.com/pub/a/1999/09/expat/index.html
# - import a single file or a whole directory
# - extract coordinates
# - Save Name and Description tags into attribute table
# - Save Elevation tag in attribute table
# - Save Timestamp in attribute table
# - use v.edit to create the output file
# - always assume valid KML
# - ignore any tags that cannot be parsed
# - produce nothing, if nothing can be parsed
# - thematic mapping support: create a unique int key value for every
#   unique symbol found in the KML
# - produce diagnostics file
*/


