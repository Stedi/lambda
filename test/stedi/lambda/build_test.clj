(ns stedi.lambda.build-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer [deftest testing is]])
  (:import [java.io ByteArrayOutputStream]))

;; Note: you have to run `clojure -m stedi.lambda.build` in the `./example` directory
;; before running these tests. These tests run against the artifacts generated by that
;; command.
(deftest build-test
  (testing "correct jars are built"
    (is (.exists (io/file "./example/target/lambda/stedi.example/hello.jar")))
    (is (.exists (io/file "./example/target/lambda/stedi.example/hello-again.jar"))))

  (testing "entrypoint is invokable"
    ;; This is gross but the classes we want to test aren't available
    ;; until this test runs so `import` will fail if we don't wait and dynamically eval
    (let [output (eval `(do
                          (with-open [is# (io/input-stream (.getBytes "world"))
                                      os# (ByteArrayOutputStream.)]
                            (import '[stedi.lambda ~'Entrypoint])
                            (stedi.lambda.Entrypoint/handler is# os# nil)
                            (str os#))))]
      (is (= "Hello, world!" output)))))
