import React, { useState } from "react";
import { Fab, Zoom } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import {Snackbar} from "@mui/base";
import Alert from "@mui/material/Alert";

function CreateArea(props) {
    const [isExpanded, setExpanded] = useState(false);
    const [open, setOpen] = useState(false)

    const [note, setNote] = useState({
        title: "",
        description: ""
    });

    function handleChange(event) {
        const { name, value } = event.target;

        setNote(prevNote => {
            return {
                ...prevNote,
                [name]: value
            };
        });
    }

    function submitNote(event) {
        if(!note.title && !note.description){
            setOpen(true)
            return
        }
        props.onAdd(note);
        setNote({
            title: "",
            description: ""
        });
        event.preventDefault();
    }

    function expand() {
        setExpanded(true);
    }

    return (
        <div>
            <form className="create-note">
                {isExpanded && (
                    <input
                        name="title"
                        onChange={handleChange}
                        value={note.title}
                        placeholder="Title"
                    />
                )}

                <textarea
                    name="description"
                    onClick={expand}
                    onChange={handleChange}
                    value={note.description}
                    placeholder="Take a note..."
                    rows={isExpanded ? 3 : 1}
                />
                <Zoom in={isExpanded}>
                    <Fab onClick={submitNote}>
                        <AddIcon />
                    </Fab>
                </Zoom>
            </form>
            <Snackbar open={open} autoHideDuration={6000} onClose={() => setOpen(false)}>
                <Alert onClose={() => setOpen(false)} severity="error" sx={{ width: '100%' }}>
                    Title and Description both can't be empty!
                </Alert>
            </Snackbar>
        </div>
    );
}

export default CreateArea;
