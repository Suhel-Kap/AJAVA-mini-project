import {useEffect, useState} from "react";
import Header from "@/components/Header";
import CreateArea from "@/components/CreateArea";
import Note from "@/components/Note";
import Footer from "@/components/Footer";
import Head from "next/head";
import axios from "axios";

export default function Home() {
    const [notes, setNotes] = useState([]);

    useEffect(() => {
        if (window !== undefined) {
            const email = sessionStorage.getItem("email")
            const loggedIn = sessionStorage.getItem("loggedIn")
            if (!(loggedIn === "true")) {
                window.location.href = "/"
            }
            axios.get("http://localhost:8080/Notes/Note", {
                params: {
                    email,
                    loggedIn
                }
            }).then(res => {
                setNotes(res.data)
            }).catch(error => {
                if (error.response.status === 502)
                    window.location.href = "/"
            })
        }
    }, [])

    function addNote(newNote) {
        axios.post("http://localhost:8080/Notes/Note", {
            email: sessionStorage.getItem("email"),
            title: newNote.title,
            description: newNote.description
        }, {
            params: {
                loggedIn: sessionStorage.getItem("loggedIn")
            }
        }).then(res => {
            setNotes(res.data)
        }).catch(error => {
            if (error.response.status === 502)
                window.location.href = "/"
        })
    }

    function deleteNote(title) {
        axios.delete("http://localhost:8080/Notes/Note", {
            params: {
                loggedIn: sessionStorage.getItem("loggedIn"),
                email: sessionStorage.getItem("email"),
                title: title
            }
        }).then(res => {
            console.log(res)
            setNotes(res.data)
        }).catch(error => {
            if (error.response.status === 502)
                window.location.href = "/"
        })
    }

    return (
        <>
            <Head>
                <title>Notes</title>
            </Head>
            <div>
                <Header/>
                <CreateArea onAdd={addNote}/>
                {notes.map((noteItem, index) => {
                    return (
                        <Note
                            key={index}
                            id={index}
                            title={noteItem.title}
                            content={noteItem.description}
                            onDelete={deleteNote}
                        />
                    );
                })}
                <Footer/>
            </div>
        </>
    );
}
