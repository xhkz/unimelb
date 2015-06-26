int: f;
int: g;
int: c;
int: k;
set of int: Cap = 0..k;
int: t;
set of int: Trips = 1..t;
int: pf;
int: pg;
int: pc;

array[Trips] of var Cap: fox;
array[Trips] of var Cap: geese;
array[Trips] of var Cap: corn;
var Trips: trips;

set of int: Trips0 = 0..t;
%west
array[Trips0] of var 0..f: wf;
array[Trips0] of var 0..g: wg;
array[Trips0] of var 0..c: wc;
%east
array[Trips0] of var 0..f: ef;
array[Trips0] of var 0..g: eg;
array[Trips0] of var 0..c: ec;

%%%%%%%%%%%%%%%%%%%%%%%%%%% ADD constraints in here %%%%%%%%%%%%%
predicate alone(var 0..f: cf, var 0..g: cg, var 0..c: cc, var 0..f: nf, var 0..g: ng, var 0..c: nc) =
let {
  var bool: x = cf != 0 /\ cg = 0  /\ cc != 0,
  var bool: y = cf = 0  /\ cg != 0 /\ cc != 0,
  var bool: z = cf != 0 /\ cg != 0
} in
  nf = cf - bool2int(x \/ (z /\ cf > cg))
  /\ ng = cg - (bool2int(z /\ cg >= cf) * cf + bool2int(y /\ cg > cc))
  /\ nc = cc - (bool2int(x) + bool2int(y /\ cg <= cc) * cg + bool2int(y /\ cg > cc));

constraint wf[0] = f /\ wg[0] = g /\ wc[0] = c
           /\ ef[0] = 0 /\ eg[0] = 0 /\ ec[0] = 0
           /\ trips mod 2 = 1;

constraint fox[trips] > 0 \/ geese[trips] > 0 \/ corn[trips] > 0;
constraint forall(i in Trips)(fox[i] + geese[i] + corn[i] <= k);
constraint forall(i in Trips)(i > trips -> fox[i] = 0 /\ geese[i] = 0 /\ corn[i] = 0);

constraint forall(i in Trips where i mod 2 = 1)(
    (ef[i] = ef[i-1] + fox[i]) /\ (eg[i] = eg[i-1] + geese[i]) /\ (ec[i] = ec[i-1] + corn[i])
    /\ (wf[i] = wf[i-1] - fox[i]) /\ (wg[i] = wg[i-1] - geese[i]) /\ (wc[i] = wc[i-1] - corn[i])
    /\ (fox[i] <= wf[i-1] /\ geese[i] <= wg[i-1] /\ corn[i] <= wc[i-1])
);

constraint forall(i in Trips where i mod 2 = 0)(
    (fox[i] <= ef[i-1] /\ geese[i] <= eg[i-1] /\ corn[i] <= ec[i-1])
    /\ alone(wf[i-1], wg[i-1], wc[i-1], wf[i]-fox[i], wg[i]-geese[i], wc[i]-corn[i])
    /\ alone(ef[i-1]-fox[i], eg[i-1]-geese[i], ec[i-1]-corn[i], ef[i], eg[i], ec[i])
);

var int: obj = let {var int: t = trips } in pf*ef[t] + pg*eg[t] + pc*ec[t];
%%%%%%%%%%%%%%%%% you may want to modify the search strategy below

solve
:: int_search([trips], input_order, indomain_min, complete)
maximize obj;

output ["fox   = ", show(fox),
  ";\ngeese = ", show(geese),
	";\ncorn  = ", show(corn),
	";\ntrips = ", show(trips), 
	";\nobj = ", show(obj), ";\n"];