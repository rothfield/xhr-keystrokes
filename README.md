In the spirit of the excellent clojurescript tutorial http://swannodette.github.io/2013/11/07/clojurescript-101/

This project listens for keystrokes in a the query input field. When the user types in the query field, an xhr request is fired and the results appear in the results area. One xhr request is sent every 6 seconds if the value in the query box changed since the last xhr request. This is similar to an autocomplete. 

```ruby
  (let [ results-view (dom/getElement "results")
        last-value (atom "") 
        keypresses (listen (dom/getElement "query") "keypress")]
    (go (while true
          (<! keypresses)
          (let [cur-value (user-query)]
            (when (not= cur-value @last-value) 
              (reset! last-value cur-value)
              (let [
                    results (<! (jsonp (str  wiki-url cur-value)))
                    ]
                (set! (.-innerHTML results-view) (render-query results))
                )
              (<! (timeout (* 6 seconds))) 
              ))
          )))
```
