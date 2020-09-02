module Ex2 
(
  mapLB,
)
where
  
import Ex1

-- ****************************************************************************
-- ****************************************************************************
-- Foldable instance
-- ****************************************************************************
-- ****************************************************************************
instance Foldable ListBag where

  -- Folding a ListBag with a binary function 
  -- should apply the function to the elements of the multiset
  -- ignoring the multiplicities.
  foldr f z (LB []) = z
  foldr f z (LB ((k, v):xs)) = foldr f (f k z) (LB xs)

  -- toList l = foldr (:) [] l
  -- length l = length (toList l)
  -- elem x l = elem x (toList l)
  -- maximum l = maximum (toList l)
  -- minimum l = minimum (toList l)
  -- sum l = sum (toList l)
  -- product l = product (toList l)


-- ****************************************************************************
-- ****************************************************************************
-- mapLB function
-- ****************************************************************************
-- ****************************************************************************

-- Define a function mapLB that takes a function f :: a -> b 
-- and a ListBag of type a as an argument,
-- and returns the ListBag of type b obtained 
-- by applying f to all the elements of its second argument.
mapLB :: Eq b => (a -> b) -> ListBag a -> ListBag b
mapLB f z = fromList (map f (toList z))

-- ****************************************************************************
-- ****************************************************************************
-- Functor instance
-- ****************************************************************************
-- ****************************************************************************

-- Explain why it is not possible to define an instance 
-- of Functor for ListBag by providing mapLB 
-- as the implementation of fmap.

-- instance Functor ListBag where
--   fmap = mapLB
-- error: "No instance for (Eq b) arising from a use of ‘mapLB’"

-- The type of fmap of Functor is
-- fmap :: (a -> b) -> f a -> f b
-- 
-- While the type of mapLB is
-- mapLB :: Eq b => (a -> b) -> ListBag a -> ListBag b
-- 
-- The Eq b contraint in mapLB doesn't allow it to be used to define a fmap 
-- The Eq b constraint is required as mapLB call fromList function