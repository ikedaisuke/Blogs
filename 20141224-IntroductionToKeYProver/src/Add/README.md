# spec

MacOSX Mavericks 10.9.5
2.53 GHz Intel Core 2 Duo
8 GB 1067 MHz DDR3

Key Prover 2.2.3
Z3 4.3.2
Yices 2.2.2

# proved: statistics

    Nodes: 163
    Branches: 1
    Interactive steps: 0
    Automodo time: 675ms
    Avg. time per step: 4.1ms

    Rule applications:
	Quantifier instantiations: 0
	One-step Simplifier apps: 21
	SMT solver apps: 0
	Dependency Contract apps: 0
	Loop invariant apps: 0
	Total rule apps: 265

# found counter example by SMT solvers

## Z3 translation

```

(set-option :print-success true)

(set-option :produce-unsat-cores true)

(set-option :produce-models true)

          ; Declaration of sorts.

(declare-sort u 0)
          ; Predicates used in formula:


(declare-fun wellFormed_3
 (u ) Bool )

(declare-fun inInt_7
 (Int ) Bool )

(declare-fun measuredByEmpty_9
 () Bool )

(declare-fun modConst_10
 () Bool )
          ; Types expressed by predicates:


(declare-fun type_of_Heap_0_1
 (u ) Bool )

(declare-fun type_of_int_4_5
 (u ) Bool )

          ; Function declarations

(declare-fun heap_2
 () u )

(declare-fun x_6
 () Int )

(declare-fun y_8
 () Int )

(declare-fun dummy_Heap_11
 () u )

(declare-fun dummy_int_12
 () Int )

(assert
 (not

  (=>
   (and

          ; Assumptions for function definitions:

    (type_of_Heap_0_1 heap_2 )

    (type_of_Heap_0_1 dummy_Heap_11 )

          ; Assumptions for sorts - there is at least one object of every sort:

    (exists
     (
      (x_0_13 u))
     (type_of_Heap_0_1 x_0_13 ))

)          ; End of assumptions.


          ; The formula to be proved:


   (=>
    (and
     (and
      (wellFormed_3 heap_2 )
      (and
       (inInt_7 x_6 )
       (inInt_7 y_8 ) ) ) measuredByEmpty_9 ) modConst_10 )
)          ; End of imply.
))          ; End of assert.


(check-sat)
          ; end of smt problem declaration
```

## Z3 solver output

```
Result: there is a counter example

sat
(model
  ;; universe for u:
  ;;   u!val!0 u!val!1 u!val!2
  ;; -----------
  ;; definitions for universe elements:
  (declare-fun u!val!0 () u)
  (declare-fun u!val!1 () u)
  (declare-fun u!val!2 () u)
  ;; cardinality constraint:
  (forall ((x u)) (or (= x u!val!0) (= x u!val!1) (= x u!val!2)))
  ;; -----------
  (define-fun modConst_10 () Bool
    false)
  (define-fun dummy_Heap_11 () u
    u!val!1)
  (define-fun x_6 () Int
    0)
  (define-fun heap_2 () u
    u!val!0)
  (define-fun measuredByEmpty_9 () Bool
    true)
  (define-fun x_0_13!0 () u
    u!val!2)
  (define-fun y_8 () Int
    1)
  (define-fun type_of_Heap_0_1 ((x!1 u)) Bool
    (ite (= x!1 u!val!0) true
    (ite (= x!1 u!val!1) true
    (ite (= x!1 u!val!2) true
      true))))
  (define-fun wellFormed_3 ((x!1 u)) Bool
    (ite (= x!1 u!val!0) true
      true))
  (define-fun inInt_7 ((x!1 Int)) Bool
    (ite (= x!1 0) true
    (ite (= x!1 1) true
      true)))
)
```

## cvc3 translation

```
( benchmark KeY_translation

:logic AUFLIA
 :extrasorts
 (u )

:notes "Predicates used in formula:"
:extrapreds
 (
  (wellFormed_3 u )
  (inInt_7 Int )
  (measuredByEmpty_9 )
  (modConst_10 ) )

:notes "Types expressed by predicates:"
:extrapreds
 (
  (type_of_Heap_0_1 u )
  (type_of_int_4_5 u ) )

:notes "Function declarations"
:extrafuns
 (
  (heap_2 u )
  (x_6 Int )
  (y_8 Int )
  (dummy_Heap_11 u )
  (dummy_int_12 Int ) )

:notes "Assumptions for function definitions:"
:assumption
 (type_of_Heap_0_1 heap_2)
:assumption
 (type_of_Heap_0_1 dummy_Heap_11)

:notes "Assumptions for sorts - there is at least one object of every sort:"
:assumption
 (exists
  (?x_0_13 u)
  (type_of_Heap_0_1 ?x_0_13))

:notes "The formula to be proved:"
:formula
 (not
  (implies
   (and
    (and
     (wellFormed_3 heap_2)
     (and
     (inInt_7 x_6)
      (inInt_7 y_8))) measuredByEmpty_9) modConst_10))
)
```

## cvc3 solver output

```
Result: there is a counter example

CVC> - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - sat
```

