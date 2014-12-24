public class Add {

  /*@ ensures y + x == \result; // commutative
    @ ensures (\forall int z; add(x, y) + z == x + add(y, z)); // associative
    @*/
  public static /*@ pure @*/ int add(int x, int y) {
    return x + y;
  }

}
