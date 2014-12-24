public class ElemIndex {

  /*@ requires a != null;
    @ ensures
    @   (\result == -1 &&
    @     (\forall int j; 0 <= j && j < a.length; a[j] != x)) ||
    @     (0 <= \result && \result < a.length && a[\result] == x &&
    @        (\forall int j; 0 <= j && j < \result; a[j] != x));
    @*/
  public static /*@ pure @*/ int elemIndex(int x, int[] a) {
    int i = 0, r = -1;

    /*@ loop_invariant
      @   a != null && 0 <= i && i <= a.length &&
      @   (\forall int j; 0 <= j && j < i; a[j] != x) &&
      @   (r == -1 || (r == i && i < a.length && a[r] == x));
      @ assignable r, i;
      @ decreases r == -1 ? a.length - i : 0;
      @*/
    while (r == -1 && i < a.length) {
      if (a[i] == x) r = i; else i++;
    }
    return r;
  }

}
