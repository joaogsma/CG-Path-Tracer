1. Comments:
#...

2. Output file:
output filename

3. Camera coordinates (Z must be greater than 0):
eye X Y Z

4. Image window:
ortho x0 y0 x1 y1
Window is defined by lower left point (x0, y0) and upper right point (x1, y1).

5. Size (image resolution):
size width height

6. Background color:
background R G B
Background color used for rays which do not hit any object.

7. Ambient light intensity (0 <= Ia <= 1):
ambient Ia

8. Point light:
lightpoint X Y Z R G B Ip
Point light at coordinates (x, y, z), with color (R, G, B) and intensity Ip (R, G, B and Ip between 0 and 1).

9. Area light:
lightarea filename R G B Ip
Area light with vertices and triangles specified in the given file, color (R, G, B) and intensity Ip (R, G, B and Ip between 0 and 1).

10. Light division resolution:
lightresolution width height
Specification of the grid in which to divide area lights. The light will be divided into (width*height) rectangles, with width being the division in the X dimension and 
height being the division in the Z dimension. If this command is present, the number of shadow rays does not matter, since this approach is used instead.

11. Maximum recursion depth (depth >= 0):
maxdepth depth

12. Number of paths per pixel (Rays/pixel, paths >= 0):
npaths paths

13. Number of shadow rays (srays >= 0):
nshadowrays srays
Maximum number of shadow rays computed per pixel (only applies to area lights). This command is irrelevant if the lightresolution command is present, since 
dividing the light into a grid is the approach used.

14. Tonemapping operator value (tmp > 0):
tonemapping tmp

15. Quadric object:
objectquadric a b c d e f g h i j R G B ka kd ks alfa Kr Kt ior
Quadric object specified by the equation Ax^2 + By^2 + Cz^2 + Dxy + Exz + Fyz + Gx + Hy + Iz + J = 0. R, G, B, ka, kd, ks, Kr and Kt values must be between 0 and 1.

16. Triangulated object:
object filename offsetX offsetY offsetZ R G B ka kd ks alfa Ks Kt ior
Triangulated object with vertices and triangles specified in the given file.
R, G, B, ka, kd, ks, Kr and Kt values must be between 0 and 1. The offset values are an optional translation of the object (0 if no translatoins is desired).

17. Files with object and area light descriptions must specify the vertices with the command 'v X Y Z' and the triangles with 'f v1 v2 v3'.
Also, the vertices must be all specified before thr triangles.