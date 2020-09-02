module Ex1 (
    ListBag(LB),
    mul,
    wf,
    aggregate,
    empty,
    singleton,
    fromList,
    isEmpty,
    toList,
    sumBag
) where

-- ListBag type constructor

data ListBag a = LB [(a, Int)]
  deriving (Show, Eq)

-- ****************************************************************************
-- ****************************************************************************
-- Utilities
-- ****************************************************************************
-- ****************************************************************************

-- should be in Operations section but is useful for the well-formed function
-- mul v bag, returning the multiplicity of v in the ListBag bag if v is an element of bag, and 0 otherwise
mul :: Eq a => a -> ListBag a -> Int
mul v (LB []) = 0
mul v (LB ((k, m):xs)) = if v == k then m else mul v (LB xs)
 
-- implement the predicate wf that applied to a ListBag returns True if and only if the argument is well-formed.
wf :: Eq a => ListBag a -> Bool
wf (LB []) = True
wf (LB ((k, v):xs)) = if ((v <= 0) || (mul k (LB xs) > 0)) then False else wf (LB xs)

-- From list to list [(v, k)]
aggregate :: Eq a => [a] -> [(a, Int)] 
aggregate [] = []
aggregate (x:xs) = (x, 1 + length (filter (==x) xs) ) : aggregate (filter (/= x) xs)

-- ****************************************************************************
-- ****************************************************************************
-- Constructor
-- ****************************************************************************
-- ****************************************************************************
  
-- empty, that returns an empty ListBag
empty :: ListBag a
empty = LB []

-- singleton v, returning a ListBag containing just one occurrence of element v
singleton :: a -> ListBag a
singleton v = LB [(v, 1)]

-- fromList lst, returning a ListBag containing all and only the elements of lst, each with the right multiplicity
fromList :: Eq a => [a] -> ListBag a
fromList lst = LB (aggregate lst)

-- ****************************************************************************
-- ****************************************************************************
-- Operations
-- ****************************************************************************
-- ****************************************************************************
  
-- isEmpty bag, returning True if and only if bag is empty
isEmpty :: ListBag a -> Bool
isEmpty (LB []) = True
isEmpty (LB a) = False

-- toList bag, that returns a list containing all the elements of the ListBag bag, each one repeated a number of times equal to its multiplicity
toList :: ListBag a -> [a]
toList (LB []) = []
toList (LB ((k, v):xs)) = (replicate v k) ++ toList (LB xs)

-- sumBag bag bag', returning the ListBag obtained by adding all the elements of bag' to bag
sumBag :: Eq a => ListBag a -> ListBag a -> ListBag a
sumBag (LB x) (LB y) = fromList ((toList (LB x)) ++ (toList (LB y)))