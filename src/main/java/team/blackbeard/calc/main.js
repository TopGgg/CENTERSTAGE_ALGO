const PIXEL_TYPE =
    {
        NONE: "N",
        WHITE: "W",
        YELLOW: "Y",
        PURPLE: "P",
        GREEN: "G"
    }

const VALID_MOSAICS =
    {
        GREEN: [PIXEL_TYPE.GREEN, PIXEL_TYPE.GREEN, PIXEL_TYPE.GREEN],
        PURPLE: [PIXEL_TYPE.PURPLE, PIXEL_TYPE.PURPLE, PIXEL_TYPE.PURPLE],
        YELLOW: [PIXEL_TYPE.YELLOW, PIXEL_TYPE.YELLOW, PIXEL_TYPE.YELLOW],
        MIXED: [PIXEL_TYPE.GREEN, PIXEL_TYPE.PURPLE, PIXEL_TYPE.YELLOW]
    }

const canvas = document.getElementById('canvas');
const context = canvas.getContext('2d');

const TOTAL_ROWS = 11;
const LONG_ROW_LENGTH = 7;

const POINTS_PER_AUTO_PIXEL = 5;
const POINTS_PER_DRIVER_PIXEL = 3;
const POINTS_PER_MOSAIC = 10;
const POINTS_PER_SET_LINE = 10;

const SET_LINES = [2, 5, 8]

const noneButton = document.getElementById("nonePixel");
const whiteButton = document.getElementById("whitePixel");
const yellowButton = document.getElementById("yellowPixel");
const purpleButton = document.getElementById("purplePixel");
const greenButton = document.getElementById("greenPixel");

const autonomousSwitch = document.getElementById("autonomousSwitch");
const rotationalSwitch = document.getElementById("rotationalSwitch");

class Pixel
{
    constructor(index, x, y, rowLength)
    {
        this.value = PIXEL_TYPE.NONE;

        this.position = { "index": index, "x": x, "y": y };
        this.graphics = { "x": 0, "y": 0, "radius": 0 };

        this.rowLength = rowLength;

        this.neighbors = [];
        this.inMosaic = false;
        this.fromAutonomous = false;
    }

    isMosaic()
    {
        // Don't bother if this pixel is already in a mosaic, not populated, or not a colored pixel.
        if (this.inMosaic || this.value == PIXEL_TYPE.NONE || this.value == PIXEL_TYPE.WHITE)
        {
            return false;
        }

        let coloredNeighbors = [];
        for (let i = 0; i < this.neighbors.length; i++)
        {
            if (this.neighbors[i].value != PIXEL_TYPE.NONE && this.neighbors[i].value != PIXEL_TYPE.WHITE)
            {
                coloredNeighbors.push(this.neighbors[i]);
            }
        }

        // If there are more or less than two colored neighbors, then this pixel is not part of a mosaic.
        if (coloredNeighbors.length != 2)
        {
            return false;
        }

        // Start tracking the colors forming this mosaic.
        let colors = [this.value];

        // If the 2 colored neighbors also only have two colored neighbors, then this may be a mosaic.
        for (let i = 0; i < coloredNeighbors.length; i++)
        {
            let neighbor = coloredNeighbors[i];

            let neighborsColoredNeighbors = [];
            for (let j = 0; j < neighbor.neighbors.length; j++)
            {
                if (neighbor.neighbors[j].value != PIXEL_TYPE.NONE && neighbor.neighbors[j].value != PIXEL_TYPE.WHITE)
                {
                    neighborsColoredNeighbors.push(neighbor.neighbors[j]);
                }
            }

            if (neighborsColoredNeighbors.length != 2)
            {
                return false;
            }

            colors.push(neighbor.value);
        }

        // Verify that the color combination forms a valid mosaic.
        colors.sort();

        for (let mosaicType in VALID_MOSAICS)
        {
            if (colors.toString() === VALID_MOSAICS[mosaicType].toString())
            {
                // Mark all pixels as in a mosaic before proceeding.
                this.inMosaic = true;
                for (let i = 0; i < coloredNeighbors.length; i++)
                {
                    coloredNeighbors[i].inMosaic = true;
                }

                return true;
            }
        }

        return false;
    }
}

class Backdrop
{

    constructor(radius)
    {
        this.pixels = [];
        this.setLines = [];

        // Create all of the pixels.
        for (let i = 0; i < TOTAL_ROWS; i++)
        {
            let rowLength = LONG_ROW_LENGTH - ((i + 1) % 2);

            for (let j = 0; j < rowLength; j++)
            {
                this.pixels.push(new Pixel(this.pixels.length, j, i, rowLength));
            }
        }

        // Set the neighbors for each pixel.
        for (let i = 0; i < this.pixels.length; i++)
        {
            let pixel = this.pixels[i];

            // Left
            if (pixel.position.x != 0)
            {
                pixel.neighbors.push(this.pixels[i - 1]);
            }

            // Right
            if (pixel.position.x != (pixel.rowLength - 1))
            {
                pixel.neighbors.push(this.pixels[i + 1]);
            }

            // Bottom
            if (pixel.position.y != 0)
            {
                if (pixel.rowLength == LONG_ROW_LENGTH)
                {
                    if (pixel.position.x != 0)
                    {
                        pixel.neighbors.push(this.pixels[i - LONG_ROW_LENGTH]);
                    }

                    if (pixel.position.x != (pixel.rowLength - 1))
                    {
                        pixel.neighbors.push(this.pixels[i - (pixel.rowLength - 1)]);
                    }
                }
                else
                {
                    pixel.neighbors.push(this.pixels[i - (LONG_ROW_LENGTH - 1)]);
                    pixel.neighbors.push(this.pixels[i - LONG_ROW_LENGTH]);
                }
            }

            // Top
            if (pixel.position.y != (TOTAL_ROWS - 1))
            {
                if (pixel.rowLength == LONG_ROW_LENGTH)
                {
                    if (pixel.position.x != 0)
                    {
                        pixel.neighbors.push(this.pixels[i + (LONG_ROW_LENGTH - 1)]);
                    }

                    if (pixel.position.x != (LONG_ROW_LENGTH - 1))
                    {
                        pixel.neighbors.push(this.pixels[i + LONG_ROW_LENGTH]);
                    }
                }
                else
                {
                    pixel.neighbors.push(this.pixels[i + (LONG_ROW_LENGTH - 1)]);
                    pixel.neighbors.push(this.pixels[i + LONG_ROW_LENGTH]);
                }
            }
        }

        // Store the graphical information for each pixel.
        let circles = [];

        let horizontal_margin = radius * 3;
        let veritical_margin = radius * 2;
        let offset = radius;
        let spacing = radius * 2.25;

        for (let i = 0; i < TOTAL_ROWS; i++)
        {
            let isLongRow = i % 2;

            for (let j = 0; j < (isLongRow ? LONG_ROW_LENGTH : LONG_ROW_LENGTH - 1); j++)
            {
                let x = horizontal_margin + j * spacing + (isLongRow ? 0 : offset);
                let y = veritical_margin + i * spacing;

                circles.push({ "x": x, "y": y, "radius": radius });
            }
        }

        // Sort the circle positions to match the underlying calculator.
        circles.sort(function (a, b)
        {
            if (a.y > b.y || (a.y == b.y && a.x < b.x))
            {
                return -1;
            }
            else
            {
                return 1;
            }
        });

        for (let i = 0; i < this.pixels.length; i++)
        {
            this.pixels[i].graphics = circles[i];
        }

        // Save the set line graphical data.
        for (let i = 0; i < SET_LINES.length; i++)
        {
            this.setLines.push({ "margin": offset, "height": veritical_margin + SET_LINES[i] * spacing });
        }
    }

    render()
    {
        context.clearRect(0, 0, canvas.width, canvas.height);

        // Draw the set lines first.
        for (let i = 0; i < this.setLines.length; i++)
        {
            context.strokeStyle = "#FFFFFF";
            context.lineWidth = 20;

            context.beginPath();
            context.moveTo(0, this.setLines[i].height);
            context.lineTo(canvas.width, this.setLines[i].height);
            context.stroke();
        }

        // Draw the pixels over the set lines.
        for (let i = 0; i < this.pixels.length; i++)
        {
            context.strokeStyle = "#000000";
            context.lineWidth = this.pixels[i].inMosaic ? 5 : 2;

            switch (this.pixels[i].value)
            {
                case PIXEL_TYPE.NONE:
                    context.fillStyle = "#A6A6A6";
                    break;
                case PIXEL_TYPE.WHITE:
                    context.fillStyle = "#EDECF2";
                    break;
                case PIXEL_TYPE.YELLOW:
                    context.fillStyle = "#F6CA44";
                    break;
                case PIXEL_TYPE.PURPLE:
                    context.fillStyle = "#AD9EDE";
                    break;
                case PIXEL_TYPE.GREEN:
                    context.fillStyle = "#6CAB47";
                    break;
            }

            drawCircle(context, this.pixels[i].graphics.x, this.pixels[i].graphics.y, this.pixels[i].graphics.radius);

            if (this.pixels[i].fromAutonomous)
            {
                context.fillStyle = "#000000";
                context.font = "24px Arial";

                context.textAlign = "center";
                context.textBaseline = "middle";

                context.fillText("A", this.pixels[i].graphics.x, this.pixels[i].graphics.y);
            }
        }
    }

    score()
    {
        // Clear the inMosaic flags for the pixels.
        for (let i = 0; i < this.pixels.length; i++)
        {
            this.pixels[i].inMosaic = false;
        }

        let totalAutonomousPixels = 0;
        let autonomousPixelScore = 0;

        let totalPixels = 0;
        let pixelScore = 0;

        let highestRow = -1;
        let totalSetLines = 0;
        let setLineScore = 0;

        let mosaicCount = 0;
        let mosaicScore = 0;

        for (let i = 0; i < this.pixels.length; i++)
        {
            if (this.pixels[i].value != PIXEL_TYPE.NONE)
            {
                // Score each pixels individually.
                totalPixels++;

                // Update the highest row if necessary.
                if (this.pixels[i].position.y > highestRow)
                {
                    highestRow = this.pixels[i].position.y;
                }

                // Increment the mosaic counter if necessary.
                if (this.pixels[i].isMosaic())
                {
                    mosaicCount++;
                }

                // Increment the autonomous pixel total.
                if (this.pixels[i].fromAutonomous)
                {
                    totalAutonomousPixels++;
                }
            }
        }

        // Calculate the score.
        pixelScore = totalPixels * POINTS_PER_DRIVER_PIXEL;
        mosaicScore = mosaicCount * POINTS_PER_MOSAIC;
        autonomousPixelScore = totalAutonomousPixels * POINTS_PER_AUTO_PIXEL;

        for (let i = 0; i < SET_LINES.length; i++)
        {
            if (highestRow >= SET_LINES[i])
            {
                totalSetLines++;
            }
        }

        setLineScore = totalSetLines * POINTS_PER_SET_LINE;

        // Put the scores in the relevant table elements.
        document.getElementById("autoPixelTotal").innerHTML = totalAutonomousPixels;
        document.getElementById("pixelTotal").innerHTML = totalPixels;
        document.getElementById("setLineTotal").innerHTML = totalSetLines;
        document.getElementById("mosaicTotal").innerHTML = mosaicCount;

        document.getElementById("autoPixelScore").innerHTML = autonomousPixelScore;
        document.getElementById("driverPixelScore").innerText = pixelScore;
        document.getElementById("setLineScore").innerText = setLineScore;
        document.getElementById("mosaicScore").innerText = mosaicScore;

        document.getElementById("totalScore").innerText = autonomousPixelScore + pixelScore + setLineScore + mosaicScore;
    }
}

function drawCircle(context, x, y, radius)
{
    context.beginPath();
    context.arc(x, y, radius, 0, 2 * Math.PI);
    context.fill();
    context.stroke();
}

function switchPlacementMode()
{
    noneButton.disabled = rotationalSwitch.checked;
    whiteButton.disabled = rotationalSwitch.checked;
    yellowButton.disabled = rotationalSwitch.checked;
    purpleButton.disabled = rotationalSwitch.checked;
    greenButton.disabled = rotationalSwitch.checked;
}

function initialize()
{
    // Calculate a valid radius for the circles.
    // 19.5 is the number of radius lengths that form a long row of circles.
    let radius = canvas.width / 19.5;

    // Generate the backdrop and its UI elements.
    var backdrop = new Backdrop(radius);
    backdrop.render();

    // Add an event listener for mouse clicks.
    addEventListener("mousedown", (event) =>
    {
        let selectedPixelType = PIXEL_TYPE.NONE;

        // If rotating pixel mode is off, determine the pixel type from the palette buttons.
        if (!rotationalSwitch.checked)
        {
            if (whiteButton.checked)
            {
                selectedPixelType = PIXEL_TYPE.WHITE;
            }
            else if (yellowButton.checked)
            {
                selectedPixelType = PIXEL_TYPE.YELLOW;
            }
            else if (purpleButton.checked)
            {
                selectedPixelType = PIXEL_TYPE.PURPLE;
            }
            else if (greenButton.checked)
            {
                selectedPixelType = PIXEL_TYPE.GREEN;
            }
        }

        // Place the pixel in the canvas.
        let rect = canvas.getBoundingClientRect();

        for (let i = 0; i < backdrop.pixels.length; i++)
        {
            let xDifference = backdrop.pixels[i].graphics.x - (event.x - rect.left);
            let yDifference = backdrop.pixels[i].graphics.y - (event.y - rect.top);

            let distance = Math.sqrt(Math.pow(xDifference, 2) + Math.pow(yDifference, 2));

            if (distance < backdrop.pixels[i].graphics.radius)
            {
                console.log(`Pixel #${backdrop.pixels[i].position.index} at (${backdrop.pixels[i].position.x}, ${backdrop.pixels[i].position.y})`);

                // If rotating pixel mode is on, determine the pixel type to go to next.
                if (rotationalSwitch.checked)
                {
                    switch (backdrop.pixels[i].value)
                    {
                        case PIXEL_TYPE.NONE:
                            selectedPixelType = PIXEL_TYPE.WHITE;
                            break;
                        case PIXEL_TYPE.WHITE:
                            selectedPixelType = PIXEL_TYPE.YELLOW;
                            break;
                        case PIXEL_TYPE.YELLOW:
                            selectedPixelType = PIXEL_TYPE.PURPLE;
                            break;
                        case PIXEL_TYPE.PURPLE:
                            selectedPixelType = PIXEL_TYPE.GREEN;
                            break;
                        case PIXEL_TYPE.GREEN:
                            selectedPixelType = PIXEL_TYPE.NONE;
                            break;
                    }
                }

                backdrop.pixels[i].value = selectedPixelType;
                backdrop.pixels[i].fromAutonomous = autonomousSwitch.checked;
            }
        }

        backdrop.score();
        backdrop.render(context)
    });
}

initialize();
