public class Max {

    /*@ requires (a != null && a.length > 0);
      @ ensures (\forall int k; 0 <= k && k < a.length; a[k] <= \result);
      @ ensures (\exists int k; 0 <= k && k < a.length; a[k] == \result);
      @*/
    public static /*@ pure @*/ int max1(int[] a) {
	int i, x = a[0];

	/*@ loop_invariant
	  @   0 <= i && i <= a.length &&
	  @     (\forall int k; 0 <= k && k < i; a[k] <= x) &&
          @     (i == 0 ==> x == a[0]) &&
	  @     (i > 0 ==> (\exists int k; 0 <= k && k < i; a[k] == x));
	  @ assignable i, x;
	  @ decreases a.length - i;
	  @*/
	for (i = 0; i < a.length; i++) {
	    if (x < a[i]) {
		x = a[i];
	    }
	}
	return x;
    }

    /*@ requires (a != null && a.length > 0);
      @ ensures (\forall int k; 0 <= k && k < a.length; a[k] <= \result);
      @ ensures (\exists int k; 0 <= k && k < a.length; a[k] == \result);
      @*/
    public static /*@ pure @*/ int max2(int[] a) {
	int i = 0, j = a.length - 1;

	/*@ loop_invariant
	  @   0 <= i && i <= j && j < a.length &&
	  @   (\forall int k; (0 <= k && k < i) || (j < k && k < a.length);
	  @     a[k] <= a[i] || a[k] <= a[j]);
          @ assignable i, j;
	  @ decreases j - i;
	  @*/
	while (i != j) {
	    if (a[i] > a[j]) {
		j--;
	    } else {
		i++;
	    }
	}
	return a[i];
    }
}
