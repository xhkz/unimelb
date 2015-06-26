int: nr; % number of reclaimers
         % number of rails = nr for stages A,B,C,D,E
         % number of rails = (nr + 1) div 2 for stage F
set of int: RECLAIMER = 1..nr;
bool: stageF; % is this stage F data
int: ns; % number of stockpiles
int: nsh; % number of ships
set of int: STOCKPILE = 1..ns;
array[STOCKPILE] of int: size; % size in 10000 tonnes
set of int: SHIP = 1..nsh;
array[STOCKPILE] of SHIP: ship; % which ship carries stockpile
int: stack_time;
int: reclaim_time;
int: reclaim_speed;
int: maxtime; % time considered
set of int: TIME = 0..maxtime;
array[SHIP] of TIME: arrival; % when ship arrives in port
int: len; % length of pad
set of int: POSITION = 0..len;

%defined
array[STOCKPILE] of TIME: stk_len = [stack_time * size[i] | i in STOCKPILE];
array[STOCKPILE] of TIME: rec_len = [reclaim_time * size[i] | i in STOCKPILE];

%variable
array[STOCKPILE] of var POSITION: westend;
array[STOCKPILE] of var POSITION: eastend = [westend[i] + size[i] | i in STOCKPILE];
array[STOCKPILE] of var TIME: stack;
array[STOCKPILE] of var TIME: endstack = [stack[i] + stk_len[i] | i in STOCKPILE];
array[STOCKPILE] of var TIME: reclaim;
array[STOCKPILE] of var TIME: finished = [reclaim[i] + rec_len[i] | i in STOCKPILE];
array[STOCKPILE] of var RECLAIMER: which;

constraint forall(sh in SHIP)(assert(exists(s in STOCKPILE)(ship[s] = sh),"Ship " ++ show(sh) ++ " has no cargo!"));

%------------------------constraints-----------------------------------
%StageA: Packing the Pad
constraint forall(i in STOCKPILE)(endstack[i] <= reclaim[i]);
constraint cumulative(stack , stk_len, size, len);
constraint forall(i, j in STOCKPILE where i < j)(
  %same time, no space overlap
  let {
    var TIME: s = max(stack[i], stack[j]),
    var TIME: e = min(finished[i], finished[j])
  } in (e > s) -> (eastend[i] <= westend[j] \/ eastend[j] <= westend[i])
);

%StageB: Reclaimer Assignment
predicate nonoverlap(var int:s1, var int:d1, var int:s2, var int:d2) =
  s1 + d1 <= s2 \/ s2 + d2 <= s1;

constraint forall(i, j in STOCKPILE where i < j /\ which[i] = which[j])
  (nonoverlap(reclaim[i], rec_len[i], reclaim[j], rec_len[j]));

%StageC: Ship Constraints
constraint forall(i in STOCKPILE)(reclaim[i] >= arrival[ship[i]]);
constraint forall(i, j in STOCKPILE where i < j /\ ship[i] = ship[j])
  (nonoverlap(reclaim[i], rec_len[i], reclaim[j], rec_len[j]));

%StageD: Reclaimer Movement
constraint forall(i, j in STOCKPILE where i < j /\ which[i] = which[j])(
  let { var int: gap = (abs(westend[i] - westend[j]) * reclaim_speed) }
  in reclaim[i] != reclaim[j]
  /\ if (reclaim[i] < reclaim[j]) then (reclaim[j] - finished[i] >= gap)
     else (reclaim[i] - finished[j] >= gap) endif
);

%StageE: Objective
array[SHIP] of var TIME: loaded = [max([finished[i] | i in STOCKPILE where ship[i] = s]) | s in SHIP];
var int: obj = sum(s in SHIP)(loaded[s] - arrival[s]);

%StageF: Rail Constraints
constraint stageF -> (
  let { int: rails = nr div 2 } in
    forall(i in 1..rails)(
      let {
        int: l_rec = 2 * i - 1,
        int: r_rec = 2 * i
      } in forall(j, k in STOCKPILE where which[j] = l_rec /\ which[k] = r_rec)
(eastend[j] <= westend[k])));

solve
%   :: int_search(loaded, smallest, indomain_min, complete)
minimize obj;

output
["westend  = ", show(westend), ";\n"] ++
["eastend  = ", show(eastend), ";\n"] ++
["stack    = ", show(stack), ";\n"] ++
["endstack = ", show(endstack), ";\n"] ++
["reclaim  = ", show(reclaim), ";\n"] ++
["finish   = ", show(finished) , ";\n"] ++
["which    = ", show(which), ";\n"] ++
["obj      = ", show(obj), ";\n"]
;
