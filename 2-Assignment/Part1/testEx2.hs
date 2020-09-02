{-# LANGUAGE TemplateHaskell #-}
import Ex1
import Ex2

import Test.HUnit
import Data.List

-- Actual tests
emptyEq :: ListBag Integer
emptyEq = empty
  
-- from Foldable
testFoldl = TestCase $ assertEqual "foldl (+) 0 (fromList [1,2,1,2,3,4,3,4,3]) == 10" (foldl (+) 0 (fromList [1,2,1,2,3,4,3,4,3])) 10
testFoldr = TestCase $ assertEqual "foldr (:) [] (fromList [1,2,1,2,3,4,3,4,3]) == [4, 3, 2, 1]" (foldr (:) [] (fromList [1,2,1,2,3,4,3,4,3])) [4, 3, 2, 1]
testLength = TestCase $ assertEqual "length (fromList [1,2,1,2,3,4,3,4,3]) == 4" (length (fromList [1,2,1,2,3,4,3,4,3])) 4
testLengthEmpty = TestCase $ assertEqual "length emptyEq == 0" (length emptyEq) 0
testElemTrue = TestCase $ assertEqual "elem 'b' (fromList \"abcdaad\") == True" (elem 'b' (fromList "abcdaad")) True
testElemFalse = TestCase $ assertEqual "elem 'e' (fromList \"abcdaad\") == False" (elem 'e' (fromList "abcdaad")) False
testMaximum = TestCase $ assertEqual "maximum (fromList \"abcdaad\") == 'd'" (maximum (fromList "abcdaad")) 'd'
testMinimum = TestCase $ assertEqual "minimum (fromList \"abcdaad\") == 'a'" (minimum (fromList "abcdaad")) 'a'

-- mapLB
testMapLB = TestCase $ assertEqual "mapLB (\\x -> mod x 2) (fromList [1,2,1,2,2,2,1,1,1,1,2,2,3,3,3,4,4]) == LB [(1,9),(0,8)]" (mapLB (\x -> mod x 2) (fromList [1,2,1,2,2,2,1,1,1,1,2,2,3,3,3,4,4])) (LB [(1,9),(0,8)])

testlist = TestList [TestLabel "testFoldl" testFoldl,
                     TestLabel "testFoldr" testFoldr,
                     TestLabel "testLength" testLength,
                     TestLabel "testLengthEmpty" testLengthEmpty,
                     TestLabel "testElemTrue" testElemTrue,
                     TestLabel "testElemFalse" testElemFalse,
                     TestLabel "testMaximum" testMaximum,
                     TestLabel "testMinimum" testMinimum,
                     TestLabel "testMapLB" testMapLB
                     ]


-- Main
main :: IO ()
main = do
  runTestTT testlist
  return ()
