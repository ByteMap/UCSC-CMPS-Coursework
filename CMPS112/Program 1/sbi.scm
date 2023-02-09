#!/afs/cats.ucsc.edu/courses/cmps112-wm/usr/racket/bin/mzscheme -qr
;; $Id: sbi.scm,v 1.4 2018-04-11 16:31:36-07 - - $
;;
;; NAME
;;    sbi.scm - silly basic interpreter
;;
;; SYNOPSIS
;;    sbi.scm filename.sbir
;;
;; DESCRIPTION
;;    The file mentioned in argv[1] is read and assumed to be an SBIR
;;    program, which is the executed.  Currently it is only printed.
;;

(define *stdin* (current-input-port))
(define *stdout* (current-output-port))
(define *stderr* (current-error-port))

;;Three hash tables are created to store the information regarding the labels' data and the variables' data.
(define *function-table* (make-hash))   ;;The key will be the line number, and the data will be the line that it corresponds to, which will be another hash map key
(define *label-table* (make-hash))      ;;The key will be the label, and the value will be the the statement it refers to (or the string it refers to).
(define *variable-table* (make-hash))   ;;The key will be the variable name, and the value will be the numbers that the name variable name is referring to.
(define *statement-table* (make-hash))  ;;The key will be the statement-name (the command), and the value will be the key as well.

(for-each
    (lambda (element)
        (hash-set! *function-table* (car element) (cadr element)))
    `(
        (e 2.718281828459045235360287471352662497757247093)
        (pi 3.141592653589793238462643383279502884197169399)
        (log10_2, 0.301029995663981195213738894724493026768189881)
        (sqrt_2, 1.414213562373095048801688724209698078569671875)
        (<>, (lambda (x y) (not (equal? x y))))
        (div, (lambda (x y) (floor (/ x y))))
        (log10, (lambda (x) (/ (log x) (log 10.0))))
        (mod, (lambda (x y) (- x (* (div x y) y))))
        (quot ,(lambda (x y) (truncate (/ x y))))
        (rem ,(lambda (x y) (- x (* (quot x y) y))))
        (+, +)
        (*, *)
        (/, /)
        (^, expt)
        (ceil ,ceiling)
        (exp ,exp)
        (floor ,floor)
        (log ,log)
        (sqrt ,sqrt)
        (-, -)
        (<, <)
        (<=, <=)
        (=, =)
        (>, >)
        (>=, >=)
        (abs, abs)
        (sin, sin)
        (cos, cos)
        (tan, tan)
        (acos, acos)
        (atan, atan)
        (asin, asin)
        (round, round)
        (log,log)
    )
)

(for-each
    (lambda(element)
        (hash-set! *statement-table* (car element) (cadr element)))
    `(
        (print, print)
        (let, (void))
        (dim, (void))
        (goto, (void))
        (if, (void))
        (input, (void))
    )
)

(define *run-file*
    (let-values
        (((dirpath basepath root?)
            (split-path (find-system-path 'run-file))))
        (path->string basepath))
)

(define (die list)
    (for-each (lambda (item) (display item *stderr*)) list)
    (newline *stderr*)
    (exit 1)
)

(define (usage-exit)
    (die `("Usage: " ,*run-file* " filename"))
)

(define (readlist-from-inputfile filename)
    (let ((inputfile (open-input-file filename)))
         (if (not (input-port? inputfile))
             (die `(,*run-file* ": " ,filename ": open failed"))
             (let ((program (read inputfile)))
                  (close-input-port inputfile)
                         program)
        )
    )
)

;;increments the line
(define (increment-line line1)
    (add1 line1)
)

;;creates the label table
(define (create-label-table program incremented-line)
    (define (find-next-label incremented-line)
        (when (not (null? program))
            (when (not (equal? incremented-line (length program)))
                (when (equal? (length (list-ref program incremented-line)) 1)
                        (find-next-label (increment-line incremented-line))
                )
                (if (equal? (length (list-ref program incremented-line)) 2)
                    (if (symbol? (car (cdr (list-ref program incremented-line))))
                        (begin (hash-set! *label-table* (car (cdr (list-ref program incremented-line))) (sub1 (car (list-ref program incremented-line))))
                            (find-next-label (increment-line incremented-line))
                        )
                        (find-next-label (increment-line incremented-line))
                    )
                    ;;Else just traverse to the next line
                    (find-next-label (increment-line incremented-line))
                )
                (when (equal? (length (list-ref program incremented-line)) 3)
                    (begin (hash-set! *label-table* (car (cdr (list-ref program incremented-line))) (sub1 (car (list-ref program incremented-line))))
                        (find-next-label (increment-line incremented-line))
                    )
                )
            )
        )
    )
    (when (not (null? program))
        (find-next-label (increment-line incremented-line))
    )
)

(define (traverse-file program incremented-line)
    (define (traverse-next incremented-line)

        ;;When the program constains nothing, exit successfully
        (when (null? program)
        (exit 0))

        ;;If the program has reached the EOF, exit successfully
        (when (equal? incremented-line (length program))
        (exit 0))

        ;;If the line only has a length of 1 (i.e only has a line number), automatically move to the next line
        (when (equal? (length (list-ref program incremented-line)) 1)
            (traverse-next (increment-line incremented-line))
        )

        ;;If the line has a length of 2, then do the following:
        (when (equal? (length (list-ref program incremented-line)) 2)
            (if (not (hash-has-key? *label-table* (car (cdr (list-ref program incremented-line)))))
                (when (hash-has-key? *statement-table* (car (car (cdr (list-ref program incremented-line)))))

                            ;;GOTO METHOD
                    (begin (when (equal? (car (car (cdr (list-ref program incremented-line)))) `goto)
                                (traverse-next (hash-ref *label-table* (car (cdr (car (cdr (list-ref program incremented-line)))))))
                            )

                            ;;IF METHOD
                            (when (equal? (car (car (cdr (list-ref program incremented-line)))) `if)

                                ;;Check if its a vector
                                (when (pair? (cadr (cadadr (list-ref program incremented-line))))
                                    (if (not (equal? (evaluate-expression (cadadr (cadadr (list-ref program incremented-line)))) 0))
                                        (if ((hash-ref *function-table* (caadr (cadr (list-ref program incremented-line))))
                                                (vector-ref (evaluate-expression (caadr (cadadr (list-ref program incremented-line)))) (sub1 (evaluate-expression (cadadr (cadadr (list-ref program incremented-line))))))
                                                (evaluate-expression (caddar (cdadr z))))
                                            (traverse-next (hash-ref *label-table* (caddr (cadr (list-ref program incremented-line)))))
                                            (traverse-next (increment-line incremented-line))
                                        )
                                        (if ((hash-ref *function-table* (caadr (cadr (list-ref program incremented-line))))
                                                (vector-ref (evaluate-expression (caadr (cadadr (list-ref program incremented-line)))) (evaluate-expression (cadadr (cadadr (list-ref program     incremented-line)))))
                                                (evaluate-expression (caddar (cdadr z))))
                                            (traverse-next (hash-ref *label-table* (caddr (cadr (list-ref program incremented-line)))))
                                            (traverse-next (increment-line incremented-line))
)
                                    )
                                )

                                ;;If not a vector
                                (cond ((when (hash-has-key? *function-table* (car (cdr (car (cdr (car (cdr (list-ref program incremented-line))))))))
                                                (when (hash-has-key? *function-table* (car (cdr (cdr (car (cdr (car (cdr (list-ref program incremented-line)))))))))
                                                        (if ((hash-ref *function-table* (caadr (cadr (list-ref program incremented-line))))
                                                                (hash-ref *function-table* (cadar (cdadr (list-ref program incremented-line))))
                                                                (hash-ref *function-table* (caddar (cdadr (list-ref program incremented-line)))))
                                                            (traverse-next (hash-ref *label-table* (caddr (cadr (list-ref program incremented-line)))))
                                                            (traverse-next (increment-line incremented-line))
                                                        )
                                                )
                                        )
                                        (when (hash-has-key? *function-table* (cadar (cdadr (list-ref program incremented-line))))
                                                (if ((hash-ref *function-table* (caadr (cadr (list-ref program incremented-line))))
                                                        (hash-ref *function-table* (cadar (cdadr (list-ref program incremented-line))))
                                                        (evaluate-expression (caddar (cdadr (list-ref program incremented-line)))))
                                                    (traverse-next (hash-ref *label-table* (caddr (cadr (list-ref program incremented-line)))))
                                                    (traverse-next (increment-line incremented-line))
                                                )
                                        )
                                        (when (hash-has-key? *function-table* (caddar (cdadr (list-ref program incremented-line))))
                                                (if ((hash-ref *function-table* (caadr (cadr (list-ref program incremented-line))))
                                                        (evaluate-expression (cadar (cdadr (list-ref program incremented-line))))
                                                        (hash-ref *function-table* (caddar (cdadr (list-ref program incremented-line)))))
                                                    (traverse-next (hash-ref *label-table* (caddr (cadr (list-ref program incremented-line)))))
                                                    (traverse-next (increment-line incremented-line))
                                                )
                                        )
                                    )
                                )
                            )
                    )
                    ;;If the key is not goto or if, then execute whatever statement
                    (begin (execute-statement (car (car (cdr (list-ref program incremented-line)))) (cdr (car (cdr (list-ref program incremented-line)))))
                            (traverse-next (increment-line incremented-line))
                    )
                )
                (traverse-next (increment-line incremented-line))
            )
        )

        ;;If the length of the program is 3, do the following
        (when (equal? (length (list-ref program incremented-line)) 3)
            (when (hash-has-key? *statement-table* (car (car (cdr (cdr (list-ref program incremented-line))))))

                ;;GOTO METHOD
                (begin (when (equal? (car (car (cdr (cdr (list-ref program incremented-line))))) `goto)
                            (traverse-next (hash-ref *label-table* (car (cdr (car (cdr (cdr (list-ref program incremented-line))))))))
                        )

                        ;;IF METHOD
                        (when (equal? (caaddr (list-ref program incremented-line)) `if)

                            ;;If it is a vector
                            (when (pair? (cadadr (caddr (list-ref program incremented-line))))
                                (if (not (equal? (evaluate-expression (car (cdadar (cdaddr (list-ref program incremented-line))))) 0))
                                    (if ((hash-ref *function-table* (caadr (caddr (list-ref program incremented-line))))
                                            (vector-ref (evaluate-expression (caadar (cdaddr (list-ref program incremented-line)))) (sub1 (evaluate-expression (car (cdadar (cdaddr (list-ref program incremented-line)))))))
                                            (evaluate-expression (caddar (cdaddr (list-ref program incremented-line)))))
                                        (traverse-next (hash-ref *label-table* (caddr (caddr (list-ref program incremented-line)))))
                                        (traverse-next (increment-line incremented-line))
                                    )
                                    (if ((hash-ref *function-table* (caadr (caddr (list-ref program incremented-line))))
                                            (vector-ref (evaluate-expression (caadar (cdaddr (list-ref program incremented-line)))) (evaluate-expression (car (cdadar (cdaddr (list-ref program incremented-line))))))
                                            (evaluate-expression (caddar (cdaddr (list-ref program incremented-line)))))
                                        (traverse-next (hash-ref *label-table* (caddr (caddr (list-ref program incremented-line)))))
                                        (traverse-next (increment-line incremented-line))
                                    )
                                )
                            )
                            (cond ((when (hash-has-key? *function-table* (cadadr (caddr (list-ref program incremented-line))))
                                            (when (hash-has-key? *function-table* (caddar (cdaddr (list-ref program incremented-line))))
                                                    (if ((hash-ref *function-table* (caadr (caddr (list-ref program incremented-line))))
                                                            (hash-ref *function-table* (cadadr (caddr (list-ref program incremented-line))))
                                                            (hash-ref *function-table* (caddar (cdaddr (list-ref program incremented-line)))))
                                                        (traverse-next (hash-ref *label-table* (caddr (caddr (list-ref program incremented-line)))))
                                                        (traverse-next (increment-line incremented-line))
                                                        )
                                                    )
                                            )
                                    (when (hash-has-key? *function-table* (cadadr (caddr (list-ref program incremented-line))))
                                            (if ((hash-ref *function-table* (caadr (caddr (list-ref program incremented-line))))
                                                    (hash-ref *function-table* (cadadr (caddr (list-ref program incremented-line))))
                                                    (evaluate-expression (caddar (cdaddr (list-ref program incremented-line)))))
                                                (traverse-next (hash-ref *label-table* (caddr (caddr (list-ref program incremented-line)))))
                                                (traverse-next (increment-line incremented-line))
                                            )
                                    )
                                    (when (hash-has-key? *function-table* (caddar (cdaddr (list-ref program incremented-line))))
                                            (if ((hash-ref *function-table* (caadr (caddr (list-ref program incremented-line))))
                                                    (evaluate-expression (cadadr (caddr (list-ref program incremented-line))))
                                                    (hash-ref *function-table* (caddar (cdaddr (list-ref program incremented-line)))))
                                                (traverse-next (hash-ref *label-table* (caddr (caddr (list-ref program incremented-line)))))
                                                (traverse-next (increment-line incremented-line))
                                            )
                                        )
                                    )
                            )
                    )
                )

                ;;Execute whatever statement if the key is not if or goto
                (begin (execute-statement (car (car (cdr (cdr (list-ref program incremented-line))))) (cdr (car (cdr (cdr (list-ref program incremented-line))))))
                        (traverse-next (increment-line incremented-line))
                )
            )
        )
    )
    (traverse-next (increment-line incremented-line))
)

;;Executes the statement
(define (execute-statement key statement)
    (when (equal? key `print)
        (if (not (null? statement))
            (if (> (length statement) 1)
                (if (string? (car statement))
                    (begin (print-method (car statement))
                            (execute-statement key (cdr statement))
                    )
                    (begin (print-method (evaluate-expression (car statement)))
                            (execute-statement key (cdr statement))
                    )
                )
                (begin (print-method (car statement))
                    (newline)
                )
            )
            (newline)
        )
    )
    (when (equal? key `let)
        (let-method (car statement) (car (cdr statement)))
    )
    (when (equal? key `dim)
        (if (equal? (length statement) 1)
            (dim-method (car (car statement)) (car (cdr (car statement))))
            (dim-method (car statement) (car (cdr statement)))
        )
    )
    (when (equal? key `input)
        (input-method statement)
    )
)

;;Evaluates the expression
(define (evaluate-expression expression)
    (cond ((number? expression)
            (if (equal? expression 0) 0.0
                expression)
            )

        ((symbol? expression)
                (hash-ref *function-table* expression #f)
        )
        ((pair? expression)
                (apply (hash-ref *function-table* (car expression))
                    (map evaluate-expression (cdr expression)))
        )
    )
)

;;Print method
(define (print-method statement)
    (if (pair? statement)
        (if (hash-has-key? *function-table* (car statement))
            (if (vector? (hash-ref *function-table* (car statement)))
                (display (vector-ref (hash-ref *function-table* (car statement)) (sub1 (hash-ref *function-table* (cadr statement)))))
                (display (evaluate-expression statement))
            )
           (display (execute-statement (statement)))
        )
        (if (hash-has-key? *function-table* statement)
            (display (hash-ref *function-table* statement))
            (display statement)
        )
    )
)

;;Let method
(define (let-method key statement)
    (cond ((symbol? key)
            (hash-set! *function-table* key (evaluate-expression statement))
        )

        ;;If it is a vector
        ((pair? key)
            (if (hash-has-key? *function-table* (car (cdr key)))

                ;;checks to see if the subscript is a 0, if not then bring the subscript down by one
                (if (not (equal? (car (cdr key)) 0))
                    (vector-set! (hash-ref *function-table* (car key)) (sub1 (hash-ref *function-table* (car (cdr key)))) (evaluate-expression statement))
                    (vector-set! (hash-ref *function-table* (car key)) (hash-ref *function-table* (car (cdr key))) (evaluate-expression statement))
                )

                ;;checks to see if the subscript is a 0, if not then bring the subscript down by one
                (if (not (equal? (car (cdr key)) 0))
                    (vector-set! (hash-ref *function-table* (car key)) (sub1 (car (cdr key))) (evaluate-expression statement))
                    (vector-set! (hash-ref *function-table* (car key)) (car (cdr key)) (evaluate-expression statement))
                )
            )
        )
    )
)

;;Dim method
(define (dim-method variable length)
    (if (hash-has-key? *function-table* length)
        (hash-set! *function-table* variable (make-vector (hash-ref *function-table* length)))
        (hash-set! *function-table* variable (make-vector length))
    )
)

;;Input method
(define (input-method variable)
    (hash-set! *function-table* `inputcount 0)
    (define (read-variable variable)
        (when (not (null? variable))
            (let ((input (read)))
                (cond ((eof-object? input)
                        (hash-set! *function-table* `inputcount -1)
                    )
                    ((pair? input)
                        (if (hash-has-ref *function-table* (car variable))
                            (if (<= (car input) (vector-length (hash-ref *function-table* variable)))
                                (if (not (equal (cadr input) 0))
                                    (begin (vector-set! (hash-ref *function-table* variable) (sub1 (car input)) (evaluate-expression (cadr input)))
                                            (hash-set! *function-table* `inputcount (+ (hash-ref *function-table* `inputcount) 1))
                                            (read-variable (cdr variable))
                                    )
                                    (begin (vector-set! (hash-ref *function-table* variable) (car input) (evaluate-expression (cadr input)))
                                            (hash-set! *function-table* `inputcount (+ (hash-ref *function-table* `inputcount) 1))
                                            (read-variable (cdr variable))
                                    )
                                )
                                (begin (display "Bounds Error: Attempting to place an element inside an array's subscript that is out of bounds")
                                        (newline)
                                )
                            )
                            (begin (display "Undefined Error: Attempting to place an element inside an array that does not exist within the function table")
                                    (newline)
                            )
                        )
                    )
                    ((number? input)
                        (begin (hash-set! *function-table* (car variable) input)
                                (hash-set! *function-table* `inputcount (+ (hash-ref *function-table* `inputcount) 1))
                                (read-variable (cdr variable))
                        )
                    )
                )
            )
        )
    )
    (read-variable variable)
)


(define (main arglist)
    (if (or (null? arglist) (not (null? (cdr arglist))))
        (usage-exit)
            (let* ((sbprogfile (car arglist))
                (program (readlist-from-inputfile sbprogfile)))
                (create-label-table program 0)
                (traverse-file program 0)
            )
    )
)

(when (terminal-port? *stdin*)
      (main (vector->list (current-command-line-arguments))))
