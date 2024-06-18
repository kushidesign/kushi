(ns kushi.ui.slider.css)

(def css
  (str "

:root{
  --kushi-input-slider-track-background-color: silver;
  --kushi-input-slider-thumb-width: 1em;
  --kushi-input-slider-thumb-height: 1em;
  --kushi-input-slider-thumb-border-radius: var(--kushi-input-slider-thumb-width);
  --kushi-input-slider-thumb-margin-top: calc( var(--kushi-input-slider-thumb-height) / -2);

  --kushi-input-slider-thumb-outline-width-ratio: 3;
  --kushi-input-slider-thumb-outline-color: #000;
  --kushi-input-slider-thumb-outline-color-dark: #fff;
  --kushi-input-slider-thumb-background-color: #fff;
  --kushi-input-slider-thumb-background-color-dark: #000;
  --kushi-input-slider-thumb-outline-style: solid;

  --kushi-input-slider-thumb-outline-width: calc( var(--kushi-input-slider-thumb-width) / var(--kushi-input-slider-thumb-outline-width-ratio));
  --kushi-input-slider-thumb-outline-offset: calc( var(--kushi-input-slider-thumb-width) / (0 - var(--kushi-input-slider-thumb-outline-width-ratio)));
  --kushi-input-slider-thumb-outline: var(--kushi-input-slider-thumb-outline-width) var(--kushi-input-slider-thumb-outline-style, solid) var(--kushi-input-slider-thumb-outline-color);
  --kushi-input-slider-thumb-outline-dark: var(--kushi-input-slider-thumb-outline-width) var(--kushi-input-slider-thumb-outline-style, solid) var(--kushi-input-slider-thumb-outline-color-dark);
}

input[type=range] {
  height: var(--kushi-input-slider-thumb-height);
  -webkit-appearance: none;
  width: 100%;
}
input[type=range]:focus {
  outline: none;
}
input[type=range]::-webkit-slider-runnable-track {
  width: 100%;
  height: 1px;
  cursor: pointer;
  animate: 0.2s;
  box-shadow: 0px 0px 0px #000000;
  background: var(--kushi-input-slider-track-background-color);
  border-radius: 1px;
  border: 0px solid #000000;
}
input[type=\"range\"]::-webkit-slider-thumb {
  box-shadow: 0px 0px 0px #000000;
  outline: var(--kushi-input-slider-thumb-outline);
  outline-offset: var(--kushi-input-slider-thumb-outline-offset);
  height: var(--kushi-input-slider-thumb-height);
  width:  var(--kushi-input-slider-thumb-width);
  border-radius: var(--kushi-input-slider-thumb-border-radius);
  background: var(--kushi-input-slider-thumb-background-color);
  cursor: pointer;
  -webkit-appearance: none;
  margin-top: calc( var(--kushi-input-slider-thumb-height) / -2);
  border-radius: 50%;
}
.dark input[type=range]::-webkit-slider-thumb {
  outline: var(--kushi-input-slider-thumb-outline-dark);
  background: var(--kushi-input-slider-thumb-background-color-dark);
}
input[type=range]:focus::-webkit-slider-runnable-track {
  background: #000;
}
.dark input[type=range]:focus::-webkit-slider-runnable-track {
  background: #fff;
}
input[type=range]::-moz-range-track {
  width: 100%;
  height: 1px;
  cursor: pointer;
  animate: 0.2s;
  box-shadow: 0px 0px 0px #000000;
  background: var(--kushi-input-slider-track-background-color);
  border-radius: 1px;
  border: 0px solid #000000;
}
.dark input[type=range]::-moz-range-track {
  border: 0px solid #fff;
}
input[type=range]::-moz-range-thumb {
  box-shadow: 0px 0px 0px #000000;
  outline: 5px solid #000;
  outline-offset: -5px;
  height: 15px;
  width: 15px;
  border-radius: 15px;
  background: #FFFFFF;
  cursor: pointer;
}
.dark input[type=range]::-moz-range-thumb {
  outline: 5px solid #fff;
  background: #000;
}
input[type=range]::-ms-track {
  width: 100%;
  height: 1px;
  cursor: pointer;
  animate: 0.2s;
  background: transparent;
  border-color: transparent;
  color: transparent;
}
input[type=range]::-ms-fill-lower {
  background: var(--kushi-input-slider-track-background-color);
  border: 0px solid #000000;
  border-radius: 2px;
  box-shadow: 0px 0px 0px #000000;
}
.dark input[type=range]::-ms-fill-lower {
  border: 0px solid #fff;
}
input[type=range]::-ms-fill-upper {
  background: var(--kushi-input-slider-track-background-color);
  border: 0px solid #000000;
  border-radius: 2px;
  box-shadow: 0px 0px 0px #000000;
}
.dark input[type=range]::-ms-fill-upper {
  border: 0px solid #fff;
}
input[type=range]::-ms-thumb {
  margin-top: 1px;
  box-shadow: 0px 0px 0px #000000;
  outline: 5px solid #000;
  outline-offset: -5px;
  height: 15px;
  width: 15px;
  border-radius: 15px;
  background: #FFFFFF;
  cursor: pointer;
}

.dark input[type=range]::-ms-thumb {
  margin-top: 1px;
  outline: 5px solid #fff;
  background: #000;
}
input[type=range]:focus::-ms-fill-lower {
  background: #000;
}
.dark input[type=range]:focus::-ms-fill-lower {
  background: #fff;
}
input[type=range]:focus::-ms-fill-upper {
  background: #000;
}
.dark input[type=range]:focus::-ms-fill-upper {
  background: #fff;
}
"))
