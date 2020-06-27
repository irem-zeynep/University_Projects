#lang scheme
;2018400063


; Solver function
(define (TENTS-SOLUTION puzzle_map)
  ( (lambda (tent_row tent_col tree_pos)
      ( (lambda (solution)
          (if (cdr solution) 
              (car solution)
              #f
              )
          )
        (match_trees (possible_pos tree_pos '() (size tent_row 0) (size tent_col 0) tree_pos) tent_row tent_col '() )
        )
      )
    (car puzzle_map)
    (cadr puzzle_map)
    (caddr puzzle_map)
    )
  )


(define (possible_pos tree_pos possible_list row_size col_size tree_list)
  (if (null? tree_pos) '()
      (cons (VALID-NEIGHBOR-LIST (NEIGHBOR-LIST (car tree_pos) ) '() row_size col_size tree_list) (possible_pos (cdr tree_pos) possible_list row_size col_size tree_list))
      )
  )


(define (match_trees possible_list row_capacity col_capacity solution)
  (if (null? possible_list) (cons solution  (finish? row_capacity col_capacity possible_list) )
      ( (lambda (valid)
          (if (not (null? valid) )
              ( (lambda (answer) 
                  (if (cdr answer) 
                      answer
                      (helper possible_list row_capacity col_capacity solution (cdr valid) )
                      ) )  
                (match_trees (delete_adjacent_tents (cdr possible_list) (car valid) row_capacity col_capacity '()) (update row_capacity (car (car valid)) (- 1) ) (update col_capacity (cadr (car valid)) (- 1) ) (cons (car valid) solution))
                )
              (cons solution (finish? row_capacity col_capacity possible_list) ) )
          ) 
        (first_valid_pos valid_pos? (car possible_list) row_capacity col_capacity)
        )
      )
  )


(define (helper possible_list row_capacity col_capacity solution options)
  (if (null? options) (cons solution  (finish? row_capacity col_capacity possible_list) )
      ( (lambda (answer)
          (if (cdr answer) 
              answer
              (helper possible_list row_capacity col_capacity solution (cdr options) ) ) )
        ( (lambda (valid)
            (if (null? valid)  (cons solution  (finish? row_capacity col_capacity possible_list) )
                (match_trees (delete_adjacent_tents (cdr possible_list) (car valid) row_capacity col_capacity '())
                             (update row_capacity (car (car valid) ) (- 1) )
                             (update col_capacity (cadr (car valid) ) (- 1) )
                             (cons (car valid) solution) ) ) )
          (first_valid_pos valid_pos? options row_capacity col_capacity)
          )
        )
      )
  )


(define (finish? tent_row tent_col possible_list)
  (and (and (check_zero tent_row) (check_zero tent_col) ) (null? possible_list) )
  )


(define (check_zero tent_left)
  (if (null? tent_left) #t
      (if (< 0 (car tent_left) ) #f
          (check_zero (cdr tent_left) )
          )
      )
  )


(define (size list1 length)
  (if (null? list1) length
      (size (cdr list1) (+ 1 length) )
      )
  )


(define (valid_pos? pos row_capacity col_capacity)
  (not (or (= 0 (at col_capacity (cadr pos) 1 ) ) (= 0 (at row_capacity (car pos) 1 ) ) ) )
  )


(define (in_map row_size col_size pos)
  (and (and (>= row_size (car pos) ) (>= col_size (cadr pos) ) ) (and (> (car pos) 0) (> (cadr pos) 0 ) ) )
  )


(define (at list1 index curr)
  (if (null? list1) 0
      (if (= index curr) (car list1)
          (at (cdr list1) index (+ 1 curr) )
          )
      )
  )


(define (contains list1 pos)
  (if (null? list1) #f
      (if (equal? (car list1) pos)
          #t
          (contains (cdr list1) pos)
          )
      )
  )


(define (delete_adjacent_tents possible_list pos row_capacity  col_capacity updated_list)
  (if (null? possible_list)
      '()
      (cons (delete_adjacent_tents_helper (car possible_list) pos row_capacity col_capacity '()) (delete_adjacent_tents (cdr possible_list) pos row_capacity  col_capacity updated_list) )
      )
  )


(define (delete_adjacent_tents_helper possible_list pos row_capacity col_capacity updated_list)
  (if (null? possible_list) updated_list
      (if (ADJACENT (car possible_list) pos)
          (delete_adjacent_tents_helper (cdr possible_list) pos row_capacity col_capacity updated_list)
          (delete_adjacent_tents_helper (cdr possible_list) pos row_capacity col_capacity (cons (car possible_list) updated_list ) )
          )
      )
  )


(define (first_valid_pos function parameters row_capacity col_capacity)
  (if (null? parameters) '()
      (if  (function (car parameters) row_capacity col_capacity) parameters
           (first_valid_pos function (cdr parameters) row_capacity col_capacity)
           )
      )
  )


(define (update list1 index num)
  (if (= index 1)
      (cons (+ num (car list1)) (cdr list1) )
      (cons (car list1) (update (cdr list1) (- index 1) num) ) ) )
          
     
(define (RETURN-FIRST-NOT-FALSE function parameters)
  (if (null? parameters) #f
      (if (function (car parameters)) (function (car parameters))
          (RETURN-FIRST-NOT-FALSE function (cdr parameters) )
          )
      )
  )


(define (REPLACE-NTH m_list index value)
  (list-set m_list (- index 1) value) 
  )


(define (ADJACENT-WITH-LIST pos p_list)
  (if (null? p_list) #f
      (if (ADJACENT pos (car p_list)) #t
          (ADJACENT-WITH-LIST pos (cdr p_list) )
          )
      )
  )


(define (VALID-NEIGHBOR-LIST neighbour_list valid_list row_size col_size tree_pos)
  (if (null? neighbour_list) valid_list
      (if (or   (contains tree_pos (car neighbour_list) )  (not (in_map row_size col_size (car neighbour_list) ) )  )
          (VALID-NEIGHBOR-LIST (cdr neighbour_list) valid_list row_size col_size tree_pos)
          (VALID-NEIGHBOR-LIST (cdr neighbour_list) (cons (car neighbour_list) valid_list)  row_size col_size tree_pos)

          )
      )
  )
  

(define (NEIGHBOR-LIST pos)
  (cons (list (car pos) (+ (- 1) (cadr pos) ) )
        (cons (list (+ (- 1) (car pos) ) (cadr pos) )
              (cons (list (car pos) (+ 1 (cadr pos) ) ) 
                    (cons (list (+ 1 (car pos) ) (cadr pos) ) '()) ) ) )
  )


(define (ADJACENT pos1 pos2) (and  (>= (- (car pos1)(car pos2)) -1)
                                   (<= (- (car pos1)(car pos2)) 1) 
                                   (>= (- (cadr pos1)(cadr pos2)) -1)
                                   (<= (- (cadr pos1)(cadr pos2)) 1 ) ) )



(define (neighbour? pos1 pos2)
  ( (lambda (right_cell same_col left_cell same_row upper_cell lower_cell)
      (or (and (or right_cell left_cell) same_col) (and (or upper_cell lower_cell) same_row) )
      )
    (equal? (car pos1) (+ 1 (car pos2) ) ) 
    (equal? (cadr pos1) (cadr pos2) )  
    (equal? (car pos1) (+ (- 1) (car pos2) ) ) 
    (equal? (car pos1) (car pos2) )
    (equal? (cadr pos1) (+ 1 (cadr pos2) ) )  
    (equal? (cadr pos1) (+ (- 1) (cadr pos2) ) ) 
    )
  )