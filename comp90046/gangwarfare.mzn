int: n;
set of int: GANGS = 1..n;
array[GANGS] of int: m;
int: r;
set of int: RIVALS = 1..r;
array[RIVALS] of GANGS: g1;
array[RIVALS] of GANGS: g2;

% all students
int: s = sum(m);
set of int: STUDENTS = 1..s;
int: mid = s div 2;

array[GANGS] of STUDENTS: leaders = [ sum([m[j] | j in 1..i-1]) + 1 | i in GANGS ];
array[GANGS] of STUDENTS: tails = [ sum([m[j] | j in 1..i]) | i in GANGS ];
array[STUDENTS] of GANGS: gang = [ i | i in GANGS, j in 1..m[i] ];
array[GANGS, GANGS] of bool: gang_rival = array2d(GANGS, GANGS,[exists(k in RIVALS)((g1[k] = i /\ g2[k] = j)\/(g2[k] = i /\ g1[k] = j))| i, j in GANGS]);

array[STUDENTS] of var STUDENTS: order;
array[STUDENTS] of var STUDENTS: pos;

constraint 
  alldifferent(order)

  /\ forall(i in 1..s-1)(gang[order[i]] != gang[order[i+1]] /\ not gang_rival[gang[order[i]], gang[order[i+1]]])

  /\ forall(i in GANGS)(forall(j, k in leaders[i]+1..tails[i] where j < k)(pos[j] + (k - j)*2 = pos[k]))

  /\ inverse(order, pos);

var int: obj =
  sum(i in GANGS)(
    let {
      STUDENTS: l = leaders[i],
      var 0..1: y = bool2int(exists(j in GANGS where gang_rival[i, j])(forall(k in leaders[j]..tails[j])(pos[l] > pos[k])))
    } in y*(3 + 2*sum(j in l+1..tails[i])(bool2int(pos[j] > pos[l]))) + (1-y)*3*bool2int(pos[l] <= mid))
  + sum(i in STUDENTS)(bool2int(pos[i] < pos[leaders[gang[i]]]))
;

solve :: int_search(order, first_fail, indomain_max, complete) maximize obj;

output [
  "order = " ++ show(order) ++ ";\n"
  ++ "obj = " ++ show(obj) ++ ";\n"
];