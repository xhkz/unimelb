int: n;
set of int: Num = 1..n;

int: m;
set of int: Group = 1..m;
set of int: Group0 = 0..m;

array[Group] of int: s;
array[Num, Num] of Group0: a;

array[Num, Num] of var Num: x;

%%%%%%%
constraint forall (i in Num) ( alldifferent([ x[i,j] | j in Num ]) );
constraint forall (j in Num) ( alldifferent([ x[i,j] | i in Num ]) );

% m*n*n
constraint forall (i in Group) ( alldifferent([x[j,k] | j, k in Num where a[j,k] = i]) );
constraint forall (i in Group) ( s[i] = sum([x[j,k] | j, k in Num where a[j,k] = i]) );
%%%%%%%

solve satisfy;

output ["x = [\n| "] ++
       [ if j = 1 /\ 2 <= i /\ i <= n then "| " else "" endif ++
         show(x[i,j]) ++ 
         if j == n then "\n" else ", " endif | i, j in Num ]
       ++
       ["|];"];