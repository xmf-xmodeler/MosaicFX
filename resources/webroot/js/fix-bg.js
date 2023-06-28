const hero = () => document.querySelector('.bg.img');

document.addEventListener('readystatechange', stateChange, false);

function stateChange(event) {
    event.target.readyState === "complete" ? domComplete() : '';
}

function domComplete() {
    // reload image
    hero().style.background = 'url("img/Terrace_Large_compressedd.jpg") 0% / cover no-repeat';

    // hope the image is ready, i know it's hacky but... ¯\_(ツ)_/¯
    setTimeout(setImageActive, 2000);
}

function setImageActive() {
    console.log('exec');
    hero().classList.add('active');
}
