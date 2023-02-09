(* $Id: bigint.ml,v 1.5 2014-11-11 15:06:24-08 - - $ *)

open Printf

module Bigint = struct

    type sign     = Pos | Neg
    type bigint   = Bigint of sign * int list
    let  radix    = 10
    let  radixlen =  1

    let car       = List.hd
    let cdr       = List.tl
    let map       = List.map
    let reverse   = List.rev
    let strcat    = String.concat
    let strlen    = String.length
    let strsub    = String.sub
    let zero      = Bigint (Pos, [])

    let charlist_of_string str = 
        let last = strlen str - 1
        in  let rec charlist pos result =
            if pos < 0
            then result
            else charlist (pos - 1) (str.[pos] :: result)
        in  charlist last []

    let bigint_of_string str =
        let len = strlen str
        in  let to_intlist first =
                let substr = strsub str first (len - first) in
                let digit char = int_of_char char - int_of_char '0' in
                map digit (reverse (charlist_of_string substr))
            in  if   len = 0
                then zero
                else if   str.[0] = '_'
                     then Bigint (Neg, to_intlist 1)
                     else Bigint (Pos, to_intlist 0)

    let string_of_bigint (Bigint (sign, value)) =
        match value with
        | []    -> "0"
        | value -> let reversed = reverse value
                   in  strcat ""
                       ((if sign = Pos then "" else "-") ::
                        (map string_of_int reversed))

    let rec cmp list1 list2 = match (list1, list2) with
        | [], []              -> 0
        | list1, []           -> 1
        | [], list2           -> -1
        | list1, list2 ->
            if List.length list1 > List.length list2 
                then 1
            else if List.length list1 < List.length list2 
                then -1
            else if car list1 > car list2
                then 1
            else if car list1 < car list2
                then -1
            else cmp (cdr list1) (cdr list2)

    let rec delete_zeroes list1 = match list1 with
        | []         -> []
        | list1      -> 
          if car list1 = 0
             then delete_zeroes (cdr list1)
          else reverse list1


    let rec add' list1 list2 carry = match (list1, list2, carry) with
        | list1, [], 0       -> list1
        | [], list2, 0       -> list2
        | list1, [], carry   -> add' list1 [carry] 0
        | [], list2, carry   -> add' [carry] list2 0
        | car1::cdr1, car2::cdr2, carry ->
          let sum = car1 + car2 + carry
          in  sum mod radix :: add' cdr1 cdr2 (sum / radix)

    let rec sub' list1 list2 borrow = match (list1, list2, borrow) with
        | list1, [], 0       -> list1
        | [], list2, 0       -> []
        | list1, [], borrow  -> 
          let diff_list1 = (car list1) - borrow in
          if diff_list1 < 0
              then diff_list1 + radix :: sub' (cdr list1) [] 1
          else diff_list1 :: sub' (cdr list1) [] 0
        | [], list2, borrow  -> []
        | car1::cdr1, car2::cdr2, borrow ->
          let difference = car1 - car2 - borrow in
          if difference < 0
              then difference + radix :: sub' cdr1 cdr2 1
          else difference :: sub' cdr1 cdr2 0

    let add (Bigint (neg1, value1)) (Bigint (neg2, value2)) =
        if neg1 = neg2
            then Bigint (neg1, add' value1 value2 0)
        else if (cmp value1 value2) > 0
            then Bigint (neg1, sub' value1 value2 0)
        else Bigint (neg2, sub' value2 value1 0)

    let sub (Bigint (neg1, value1)) (Bigint (neg2, value2)) =
        if neg1 = neg2
            then match neg1 with 
                 | Pos    ->
                   if (cmp (reverse value1) (reverse value2)) > 0
                     then Bigint (neg1, delete_zeroes 
                                       (reverse (sub' value1 value2 0)))
                     else Bigint (Neg, delete_zeroes 
                                       (reverse (sub' value2 value1 0)))
                 | Neg    ->
                   if (cmp (reverse value1) (reverse value2)) > 0
                     then Bigint (neg1, delete_zeroes 
                                 (reverse (sub' value1 value2 0)))
                     else Bigint (Pos, delete_zeroes 
                                 (reverse (sub' value2 value1 0)))

        else if (cmp (reverse value1) (reverse value2)) < 0
            then Bigint (neg2, add' value2 value1 0)

        else Bigint (neg1, add' value1 value2 0)

    let double number = add' number number 0

    let rec mul' multiplier powerof2 multiplicand =
        if (cmp (reverse powerof2) (reverse multiplier)) > 0
            then multiplier, [0]
        else let remainder, product =
                 mul' (multiplier) (double powerof2) 
                      (double multiplicand)
             in  if (cmp (reverse remainder) (reverse powerof2)) < 0
                 then remainder, product
             else (delete_zeroes (reverse (sub' remainder powerof2 0))), 
                  (add' product multiplicand 0)

    let mul (Bigint (neg1, multiplier)) (Bigint (neg2, multiplicand)) =
        if neg1 = neg2 
            then match neg1 with 
                 | Pos    ->
                   let _, product = mul' multiplier [1] multiplicand
                   in Bigint (neg1, product)
                 | Neg    ->
                   let _, product = mul' multiplier [1] multiplicand
                   in Bigint (Pos, product)
        else let _, product = mul' multiplier [1] multiplicand
             in Bigint (Neg, product)

    let rec divrem' dividend powerof2 divisor =
        if (cmp (reverse divisor) (reverse dividend)) > 0
           then [0], dividend
           else let quotient, remainder = 
                    divrem' dividend (double powerof2) (double divisor)
                in if (cmp (reverse remainder) (reverse divisor)) < 0
                   then quotient, remainder
                   else (add' quotient powerof2 0), 
                        (delete_zeroes (reverse 
                                       (sub' remainder divisor 0)))

    let divrem dividend divisor =
        divrem' dividend [1] divisor

    let div (Bigint (neg1, dividend)) (Bigint (neg2, divisor)) =
        if neg1 = neg2
           then match neg1 with 
                | Pos    ->
                  let quotient, _ = divrem dividend divisor 
                  in Bigint (neg1, quotient)
                | Neg    ->
                  let quotient, _ = divrem dividend divisor
                  in Bigint (Pos, quotient)
        else let quotient, _ = divrem dividend divisor 
             in Bigint (Neg, quotient)

    let rem (Bigint (neg1, dividend)) (Bigint (neg2, divisor)) =
        match (neg1, neg2) with
        | Pos, Pos -> let _, remainder = divrem dividend divisor
                      in Bigint (Pos, remainder)
        | Neg, Neg -> let _, remainder = divrem dividend divisor 
                      in Bigint (Neg, remainder)
        | Pos, Neg -> let _, remainder = divrem dividend divisor 
                      in Bigint (Pos, remainder)
        | Neg, Pos -> let _, remainder = divrem dividend divisor 
                      in Bigint (Neg, remainder)

    let even number = 
        let _, remainder = divrem number [2] 
        in if remainder = [] then true
           else false

    let rec pow' base expt result = match expt with
        | [0]        -> result
        | expt       ->
          if (even expt) then
             pow' (let _, product = mul' base [1] base in product) 
                  (let quotient, _ = divrem expt [2] in quotient) 
                  result
          else pow' base (sub' expt [1] 0)
                    (let _, product = mul' base [1] result in product)

    let pow (Bigint (neg1, base)) (Bigint (neg2, expt)) =
        match (neg1, neg2) with
        | Pos, Pos -> let power = pow' base expt [1] 
                      in Bigint (Pos, power)
        | Neg, Neg -> Bigint (Pos, [0])
        | Pos, Neg -> Bigint (Pos, [0])
        | Neg, Pos ->
          if (even expt) then 
                         let power = pow' base expt [1] 
                         in Bigint (Pos, power)
          else let power = pow' base expt [1] 
               in Bigint (Neg, power)

end
